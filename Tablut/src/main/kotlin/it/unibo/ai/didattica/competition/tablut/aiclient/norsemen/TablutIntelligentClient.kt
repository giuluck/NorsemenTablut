package it.unibo.ai.didattica.competition.tablut.aiclient.norsemen

import aima.core.search.adversarial.AdversarialSearch
import it.unibo.ai.didattica.competition.tablut.game.board.isTerminal
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * An artificial intelligence based implementation of the Tablut client.
 */
open class TablutIntelligentClient(
    player: String,
    name: String,
    timeout: Int,
    ipAddress: String,
    private val resolutiveStrategy: AdversarialSearch<State, Action>
) : TablutClient(player, name, timeout, ipAddress) {

    override fun run() {
        super.run()
        declareName()
        do {
            read()
            if (currentState.turn == player) {
                write(resolutiveStrategy.makeDecision(currentState))
            }
        } while (!currentState.turn.isTerminal)
    }
}