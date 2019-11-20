package it.unibo.ai.didattica.competition.tablut.aiclient

import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.legalMovesForCoord
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.playerCoords
import it.unibo.ai.didattica.competition.tablut.client.TablutClient

class TablutWellDoneRandomClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutClient(player, "Norsemen", timeout, ipAddress) {

    override fun run() {
        declareName()
        while(true) {
            read()
            with (currentState) {
                if (turn == player) {
                    write(legalMovesForCoord(playerCoords(turn).random(), AshtonTablut.movementRules(this)).random())
                }
            }
        }
    }
}