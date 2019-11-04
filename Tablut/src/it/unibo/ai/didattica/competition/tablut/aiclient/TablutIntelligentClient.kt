package it.unibo.ai.didattica.competition.tablut.aiclient

import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.Game
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut
import it.unibo.ai.didattica.competition.tablut.domain.State
import java.io.IOException
import kotlin.system.exitProcess

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
    
    override fun run() {
        declareName()
        while(true) {
            read()
            write(computeNextMove())
        }
    }

    private fun computeNextMove(): Action = TODO()
}