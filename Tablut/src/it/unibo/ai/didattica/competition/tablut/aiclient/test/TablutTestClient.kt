package it.unibo.ai.didattica.competition.tablut.aiclient.test

import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.isTerminal
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn.*

class TablutTestClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutClient(player, "Norsemen", timeout, ipAddress) {
    private val moves: List<Action> =
        if (player == "black") listOf("d1" to "c1", "c1" to "b1", "b1" to "c1").map { Action(it.first, it.second, BLACK) }
        else listOf("e4" to "d4", "e3" to "d3", "e5" to "e3", "e3" to "i3").map { Action(it.first, it.second, WHITE) }

    private var move: Int = 0

    override fun run() {
        declareName()
        do {
            read()
            with (currentState) {
                if (turn == player) {
                    write(moves[move])
                    move++
                }
            }
        }
        while (!currentState.turn.isTerminal)
    }
}