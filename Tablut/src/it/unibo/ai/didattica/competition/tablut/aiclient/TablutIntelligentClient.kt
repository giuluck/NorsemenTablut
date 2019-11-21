package it.unibo.ai.didattica.competition.tablut.aiclient

import aima.core.search.adversarial.AdversarialSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.isTerminal
import it.unibo.ai.didattica.competition.tablut.aiclient.test.toConsole
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
open class TablutIntelligentClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost",
    private val resolutiveStrategy: AdversarialSearch<State, Action>
) : TablutClient(player, "Norsemen", timeout, ipAddress) {

    override fun run() {
        declareName()
        do {
            read()
            if (currentState.turn == player) {
                write(resolutiveStrategy.makeDecision(currentState))
            }
        } while (!currentState.turn.isTerminal)
    }
}