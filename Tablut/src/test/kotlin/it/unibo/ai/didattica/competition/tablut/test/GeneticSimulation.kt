package it.unibo.ai.didattica.competition.tablut.test

import aima.core.search.framework.problem.GoalTest
import aima.core.search.local.GeneticAlgorithmForNumbers
import aima.core.search.local.Individual
import it.unibo.ai.didattica.competition.tablut.aiclient.TablutIterativeDeepeningClient
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.*
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.simulation.SimulationFunction
import it.unibo.ai.didattica.competition.tablut.util.toFile
import kotlin.math.floor
import kotlin.math.log2

const val MAX_TIME_SECONDS: Int = 24 * 60 * 60 // 1 day
val UNREACHABLE_GOAL: GoalTest = GoalTest { false }

fun main() {
    val initialPopulation: List<Individual<Double>> = (1..15).filter {
        log2(it.toDouble()).let { exp -> exp != floor(exp) } // Exclude powers of 2
    }.map { number ->
        generateSequence(number) { it / 2 }.map { weight -> weight % 2 }.take(4).toList() // Binary representation
    }.map { weights -> GeneticHeuristic(weights[0], weights[1], weights[2], weights[3]) }
    .map { it.individual }

    val whiteWeights = geneticallyComputedWeights(
        initialPopulation,
        listOf(TablutIterativeDeepeningClient("black", "GeneticSimulation", 30))
    )
    "$whiteWeights".toFile("white.txt")

    val blackWeights = geneticallyComputedWeights(
        initialPopulation,
        listOf(TablutIterativeDeepeningClient("white", "GeneticSimulation", 30))
    )
    "$blackWeights".toFile("black.txt")
}

private fun geneticallyComputedWeights(
    initialPopulation: List<Individual<Double>>,
    opponents: List<TablutClient>
): List<Double> = GeneticAlgorithmForNumbers(4, 0.0, 100.0, 0.1).geneticAlgorithm(
    initialPopulation,
    SimulationFunction(opponents),
    UNREACHABLE_GOAL,
    MAX_TIME_SECONDS * 1000L
).let { GeneticHeuristic(it).weights }