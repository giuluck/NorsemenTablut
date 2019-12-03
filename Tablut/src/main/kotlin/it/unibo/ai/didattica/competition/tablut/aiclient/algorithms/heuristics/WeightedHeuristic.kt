package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

import it.unibo.ai.didattica.competition.tablut.aiclient.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * An heuristic which combines multiple heuristics with a weighted sum.
 */
class WeightedHeuristic(private vararg val heuristics: Pair<Heuristic, Double>) : Heuristic {
    private val weightSum: Double = heuristics.map { it.second }.sum()

    override fun evaluate(game: TablutGame, state: State, player: State.Turn): Double =
        heuristics.sumByDouble { (heuristic, weight) -> heuristic.evaluate(game, state, player) * weight } / weightSum
}

