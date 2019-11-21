package it.unibo.ai.didattica.competition.tablut.aiclient.test

import it.unibo.ai.didattica.competition.tablut.aiclient.TablutIterativeDeepeningClient
import it.unibo.ai.didattica.competition.tablut.aiclient.TablutMonteCarloClient
import it.unibo.ai.didattica.competition.tablut.aiclient.TablutWellDoneRandomClient
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn
import it.unibo.ai.didattica.competition.tablut.server.Server
import kotlinx.coroutines.*

fun main() = runBlocking {
    println("Test vs Test")
    benchmark { TablutIterativeDeepeningClient("white", timeout = 7) vs TablutWellDoneRandomClient("black") }.toConsole()
}

private suspend fun benchmark(matches: Int = 10, players: () -> Pair<TablutClient, TablutClient>): Stats = Stats().apply {
    repeat(matches) {
        println("\nMATCH ${it + 1}:")
        add(singleMatch(players))
    }
}

private suspend fun singleMatch(players: () -> Pair<TablutClient, TablutClient>): Turn = withContext(Dispatchers.Default) {
    async { Server(60, -1, 0, 0, 4, true).apply {
        println("Start server...")
        run()
        println("Stop server.")
    } }.also {
        delay(1000)
        println("Start players... ")
        players().toList().map {
            launch { it.run() }
        }.forEach {
            it.join()
        }
        println("Stop players.")
    }.await().currentState.turn.apply { println("Match ended with result $this") }
}

private infix fun TablutClient.vs(opponent: TablutClient): Pair<TablutClient, TablutClient> = this to opponent

class Stats {
    private val victories: MutableMap<Turn, Int> = Turn.values().map { it to 0 }.toMap().toMutableMap()

    fun add(turn: Turn) {
        victories.merge(turn, 1) { i, j -> i + j }
    }

    fun percentage(turn: Turn): Double =
        victories.getOrDefault(turn, 0).toDouble() / victories.values.sum()

    override fun toString(): String =
        Turn.values().joinToString("\n") { "$it: ${100 * percentage(it)}%" }
}