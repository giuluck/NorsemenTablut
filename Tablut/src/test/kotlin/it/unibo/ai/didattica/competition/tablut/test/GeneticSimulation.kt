package it.unibo.ai.didattica.competition.tablut.test

import aima.core.search.framework.problem.GoalTest
import aima.core.search.local.GeneticAlgorithmForNumbers
import aima.core.search.local.Individual
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.*
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.simulation.SimulationFunction
import kotlin.math.floor
import kotlin.math.log2

const val MAX_SECONDS: Int = 2 * 60 * 60
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
        listOf(NorsemenHeuristic.blackPlayer)
    )
    println("White: $whiteWeights")

    val blackWeights = geneticallyComputedWeights(
        initialPopulation,
        listOf(NorsemenHeuristic.whitePlayer)
    )
    println("Black: $blackWeights")
}

private fun geneticallyComputedWeights(
    initialPopulation: List<Individual<Double>>,
    opponents: List<TablutClient>
): List<Double> = GeneticAlgorithmForNumbers(4, 0.0, 100.0, 0.1).geneticAlgorithm(
    initialPopulation,
    SimulationFunction(opponents),
    UNREACHABLE_GOAL,
    MAX_SECONDS * 1000L
).let { GeneticHeuristic(it).weights }