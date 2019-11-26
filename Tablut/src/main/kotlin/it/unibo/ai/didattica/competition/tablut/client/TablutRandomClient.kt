package it.unibo.ai.didattica.competition.tablut.client

import it.unibo.ai.didattica.competition.tablut.aiclient.game.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.isTerminal

class TablutRandomClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutClient(player, "Norsemen", timeout, ipAddress) {

    private val game: TablutGame = AshtonTablut()

    override fun run() {
        declareName()
        do {
            read()
            with (currentState) {
                if (turn == player) {
                    write(allLegalMoves(game.movementRules).random())
                }
            }
        }
        while (!currentState.turn.isTerminal)
    }
}