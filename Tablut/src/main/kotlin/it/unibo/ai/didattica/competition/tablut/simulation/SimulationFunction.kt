package it.unibo.ai.didattica.competition.tablut.simulation

import aima.core.search.local.FitnessFunction
import aima.core.search.local.Individual
import it.unibo.ai.didattica.competition.tablut.aiclient.norsemen.algorithms.heuristics.GeneticHeuristic
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn

/**
 * A fitness function to estimate weights of an heuristic, based on the outcomes of simulated matches.
 */
class SimulationFunction(
    private val benchmarkPlayers: List<TablutClient>
) : FitnessFunction<Double> {
    private val simulation: Simulation = TablutSimulation()
    private val computedValues: MutableMap<GeneticHeuristic, Double> = mutableMapOf()

    override fun apply(individual: Individual<Double>): Double = with(GeneticHeuristic(individual)) {
        computedValues.getOrPut(this) {
            benchmarkPlayers.map { opponent ->
                if (opponent.player == Turn.BLACK) {
                    simulation.singleMatch(whitePlayer vs opponent, false)
                } else {
                    simulation.singleMatch(opponent vs blackPlayer, false)
                }
            }.flatResults(whitePlayer, blackPlayer)
            .map { (outcome, moves) -> outcomeWeight(outcome, moves) }
            .sum()
        }
    }

    private fun outcomeWeight(outcome: Outcome, moves: Double): Double = with(moves.normalized()) {
        /*
         * +---------------+-------------+-------------+-------------+
         * |               | WIN [7, 10] | DRAW [2, 3] | LOSE [0, 1] |
         * +---------------+-------------+-------------+-------------+
         * | outcome value |      10     |      3      |      0      |
         * | moves bonus   |      -3     |     -2      |      1      |
         * +---------------+-------------+-------------+-------------+
         */
        when (outcome) {
            Outcome.WIN -> 10.0 - 3.0 * this
            Outcome.DRAW -> 3.0 - this
            Outcome.LOSE -> this
        }
    }

    private fun Double.normalized(min: Double = 15.0, max: Double = 60.0): Double = when {
        this <= min -> min
        this >= max -> max
        else -> 1.0 * (this - min) / (max - min)
    }
}