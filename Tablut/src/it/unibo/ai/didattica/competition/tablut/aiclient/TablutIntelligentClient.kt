package it.unibo.ai.didattica.competition.tablut.aiclient

import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutPlayer
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * An artificial intelligence based implementation of the Tablut client.
 *
 * @param player
 *      the role of the player in the game; it must be "white" or "black"
 * @param timeout
 *      maximum time in seconds during which the client can compute the next move.
 * @param ipAddress
 *      the address of the server where the game will run.
 */
class TablutIntelligentClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutClient(player, "Norsemen", timeout, ipAddress) {
    private val strategy = object : IterativeDeepeningAlphaBetaSearch<State, Action, TablutPlayer> (
        AshtonTablut(), -1.0, 1.0, timeout
    ) {
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

    override fun run() {
        declareName()
        while(true) {
            read()
            write(computeNextMove())
        }
    }

    private fun computeNextMove(): Action = TODO()
}