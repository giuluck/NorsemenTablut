package it.unibo.ai.didattica.competition.tablut.client

import it.unibo.ai.didattica.competition.tablut.game.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.game.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.game.board.isTerminal

/**
 * A Tablut client who selects randomly the next move to perform.
 */
class TablutRandomClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutClient(player, "Random", timeout, ipAddress) {

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