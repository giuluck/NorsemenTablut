package it.unibo.ai.didattica.competition.tablut.aiclient

import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.allMoves
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.isTerminal
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.MovementRule
import it.unibo.ai.didattica.competition.tablut.client.TablutClient

class TablutWellDoneRandomClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutClient(player, "Norsemen", timeout, ipAddress) {
    val movementRules: Set<MovementRule> by lazy {
        AshtonTablut.movementRules(currentState)
    }

    override fun run() {
        declareName()
        do {
            read()
            with (currentState) {
                if (turn == player) {
                    write(allLegalMoves(movementRules).random())
                }
            }
        }
        while (!currentState.turn.isTerminal)
    }
}