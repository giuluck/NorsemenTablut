package it.unibo.ai.didattica.competition.tablut.simulation

import aima.core.search.framework.problem.GoalTest
import aima.core.search.local.FitnessFunction
import aima.core.search.local.GeneticAlgorithmForNumbers
import aima.core.search.local.Individual
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.*
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.util.toConsole

const val MAX_SECONDS: Int = 2 * 60 * 60
val UNREACHABLE_GOAL: GoalTest = GoalTest { false }
val SIMULATION: Simulation = TablutSimulation()
val BENCHMARK_PLAYERS: List<Pair<TablutClient, TablutClient>> = listOf(
    GeneticHeuristic(1, 1, 1, 1).players
)

fun outcomeWeight(outcome: Outcome, moves: Double): Double = with(moves.normalized()) {
    /*
     * +---------------+-------------+-------------+-------------+
     * |               | WIN [7, 10] | DRAW [2, 3] | LOSE [0, 1] |
     * +---------------+-------------+-------------+-------------+
     * | outcome value |      10     |      3      |      0      |
     * | moves bonus   |      -6     |     -2      |      1      |
     * +---------------+-------------+-------------+-------------+
     */
    when (outcome) {
        Outcome.WIN -> 10.0 - 6.0 * this
        Outcome.DRAW -> 3.0 - 2.0 * this
        Outcome.LOSE -> this
    }
}

fun main() {
    val initialPopulation: List<GeneticHeuristic> = (1..15).map { number ->
        generateSequence(number) { it / 2 }.map { weight -> weight % 2 }.take(4).toList()
    }.map { weights -> GeneticHeuristic(weights[0], weights[1], weights[2], weights[3]) }

    val fitnessFunction = object : FitnessFunction<Double> {
        private val computedValues: MutableMap<GeneticHeuristic, Double> = mutableMapOf()

        override fun apply(individual: Individual<Double>): Double = with(GeneticHeuristic(individual)) {
            computedValues.getOrPut(this) {
                val (whitePlayer, blackPlayer) = players
                BENCHMARK_PLAYERS.flatMap { (whiteOpponent, blackOpponent) -> listOf(
                    SIMULATION.singleMatch(whitePlayer to blackOpponent, true),
                    SIMULATION.singleMatch(whiteOpponent to blackOpponent, true)
                ) }.flatResults(whitePlayer, blackPlayer)
                    .mapValues { (outcome, moves) -> outcomeWeight(outcome, moves) }
                    .values
                    .average()
            }
        }
    }

    GeneticAlgorithmForNumbers(4, 0.0, 100.0, 0.1).geneticAlgorithm(
        initialPopulation.map { it.individual },
        fitnessFunction,
        UNREACHABLE_GOAL,
        MAX_SECONDS * 1000L
    ).let { GeneticHeuristic(it).weights }.toConsole()
}

fun Double.normalized(min: Double = 0.0, max: Double = 50.0): Double = when {
    this <= min -> min
    this >= max -> max
    else -> 1.0 * (this - min) / (max - min)
}