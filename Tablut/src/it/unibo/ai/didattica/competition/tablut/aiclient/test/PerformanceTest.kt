package it.unibo.ai.didattica.competition.tablut.aiclient.test

import it.unibo.ai.didattica.competition.tablut.aiclient.TablutIterativeDeepeningClient
import it.unibo.ai.didattica.competition.tablut.aiclient.TablutMonteCarloClient
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn
import it.unibo.ai.didattica.competition.tablut.server.Server
import kotlinx.coroutines.*

suspend fun main() {
    val stats = benchmark(3) { TablutTestClient("white") vs TablutTestClient("black") }
    println()
    println(stats)
}

private suspend fun benchmark(matches: Int = 10, players: () -> Pair<TablutClient, TablutClient>): Stats = Stats().apply {
    repeat(matches) {
        singleMatch(players).apply {
            add(this)
            println("Match ${it + 1}: $this")
        }
    }
}

private suspend fun singleMatch(players: () -> Pair<TablutClient, TablutClient>): Turn = withContext(Dispatchers.Default) {
    Server(60, -1, 0, 0, 4, false).also { server ->
        mutableListOf<Job>().apply {
            add(launch { server.run() })
            players().toList().map {
                delay(1000)
                add(launch { it.run() })
            }
        }.forEach { it.join() }
    }.currentState.turn
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