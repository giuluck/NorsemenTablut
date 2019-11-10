package it.unibo.ai.didattica.competition.tablut.aiclient

import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutPlayer
import it.unibo.ai.didattica.competition.tablut.domain.*

/**
 * A Tablut intelligent client using a variant of iterative deepening Minimax search with alpha-beta pruning and
 * action ordering as the resolution strategy.
 */
class TablutIterativeDeepeningClient(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutIntelligentClient(
    player,
    timeout,
    ipAddress,
    object : IterativeDeepeningAlphaBetaSearch<State, Action, TablutPlayer> (AshtonTablut(), -1.0, 1.0, timeout) {
    // TODO
    override fun isSignificantlyBetter(newUtility: Double, utility: Double): Boolean =
            super.isSignificantlyBetter(newUtility, utility)

    // TODO
    override fun hasSafeWinner(resultUtility: Double): Boolean =
            super.hasSafeWinner(resultUtility)

    // TODO
    override fun eval(state: State, player: TablutPlayer): Double =
            super.eval(state, player)
    }
)