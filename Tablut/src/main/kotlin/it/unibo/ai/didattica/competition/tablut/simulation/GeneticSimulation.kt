package it.unibo.ai.didattica.competition.tablut.simulation

import aima.core.search.framework.problem.GoalTest
import aima.core.search.local.FitnessFunction
import aima.core.search.local.GeneticAlgorithmForNumbers
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.*
import it.unibo.ai.didattica.competition.tablut.util.toConsole

const val MAX_SECONDS: Int = 1 // 2 * 60 * 60
val UNREACHABLE_GOAL: GoalTest = GoalTest { false }

fun main() {
    val benchmarkPopulation = listOf(
        // GeneticHeuristic(1, 0, 0, 0),
        // GeneticHeuristic(0, 1, 0, 0),
        // GeneticHeuristic(0, 0, 1, 0),
        // GeneticHeuristic(0, 0, 0, 1),
        GeneticHeuristic(1, 1, 1, 1)
    )

    val fitness = FitnessFunction<Double> { individual ->
        val players = GeneticHeuristic(individual).players
        val simulation = TablutSimulation()
        val results = benchmarkPopulation.map { it.players }.map { (whiteOpponent, blackOpponent) ->
            simulation.singleMatch(players.first to blackOpponent, false) to simulation.singleMatch(whiteOpponent to players.second, false)
        }.flatMap { it.toList() }
        with(results.endings { Triple(100.0 / it, 0.0, -50.0 / it) }) {
            listOf(getValue(players.first), getValue(players.second))
                .map { (wins, _, loses) -> wins + loses }
                .sum()
        }
    }

    val bestIndividual = GeneticAlgorithmForNumbers(4, 0.0, 100.0, 0.1).geneticAlgorithm(
        benchmarkPopulation.map { it.individual },
        fitness,
        UNREACHABLE_GOAL,
        MAX_SECONDS * 1000L
    )

    GeneticHeuristic(bestIndividual).weights.joinToString(", ").toConsole()
}