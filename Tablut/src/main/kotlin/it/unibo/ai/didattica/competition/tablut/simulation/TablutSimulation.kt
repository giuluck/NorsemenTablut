package it.unibo.ai.didattica.competition.tablut.simulation

import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.server.SmartServer
import it.unibo.ai.didattica.competition.tablut.server.SmartServer.Stats
import kotlinx.coroutines.*

/**
 * Concrete implementation of an Ashton-based Tablut game simulation.
 */
class TablutSimulation(
    private val moveTimeout: Int = 60,
    private val debugMode: Boolean = true
) : Simulation {

    override fun singleMatch(
        players: Pair<TablutClient, TablutClient>,
        gui: Boolean
    ): Stats = runBlocking {
        withContext(Dispatchers.Default) {
            async {
                SmartServer(moveTimeout).also { it.enableGui = gui }.apply {
                    debug("Start server...")
                    run()
                    debug("Stop server.")
                }
            }.also {
                delay(1000)
                players.toList().map {
                    debug("Start ${it.name} (${it.player}) client... ")
                    launch { it.run() }
                }.forEach {
                    it.join()
                }
                debug("Stop clients.")
            }.await().stats.apply { debug(this.toString() + "\n") }
        }
    }

    private fun debug(line: String): Unit = line.takeIf { debugMode }.run { println(this) }
}