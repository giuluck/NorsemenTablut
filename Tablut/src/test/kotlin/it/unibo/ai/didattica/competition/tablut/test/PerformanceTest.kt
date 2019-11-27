package it.unibo.ai.didattica.competition.tablut.test

import it.unibo.ai.didattica.competition.tablut.aiclient.TablutIterativeDeepeningClient
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.*
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.client.TablutRandomClient
import it.unibo.ai.didattica.competition.tablut.server.SmartServer
import it.unibo.ai.didattica.competition.tablut.server.SmartServer.Stats
import it.unibo.ai.didattica.competition.tablut.server.SmartServer.Stats.Companion.percentage
import it.unibo.ai.didattica.competition.tablut.util.orThrow
import it.unibo.ai.didattica.competition.tablut.util.toFile
import kotlinx.coroutines.*
import java.time.LocalDateTime

fun debug(line: String): String = line//.apply { toConsole() } // comment out apply to avoid prints

fun main() {
    listOf(
        KingDistance() to "white",
        KingDistance() to "black",
        KingStrategicPosition() to "white",
        KingStrategicPosition() to "black",
        KingSurrounded() to "white",
        KingSurrounded() to "black",
        PawnsDifference() to "white",
        PawnsDifference() to "black"
    ).map { it.result() }.joinToString("\n") { "- $it" }.toFile("benchmarks/${LocalDateTime
        .now()
        .toString()
        .replace("-", "")
        .replace(":", "")
        .replace(".", "")
    }.txt")
}

private class Result (
    val heuristic: String,
    val player: String,
    val winningPercentage: Double,
    val averageMoves: Double
) {
    override fun toString(): String =
        "$heuristic ($player): $winningPercentage% victories, $averageMoves moves on average;"
}

private fun Pair<Heuristic, String>.result(timeout: Int = 40): Result = benchmark(players = {
    when (second) {
        "white" -> TablutIterativeDeepeningClient(
            player = "white",
            timeout = timeout,
            heuristic = first
        ) vs TablutRandomClient(player = "black")
        "black" -> TablutRandomClient("white") vs TablutIterativeDeepeningClient(
            player = "black",
            timeout = timeout,
            heuristic = first
        )
        else -> throw IllegalArgumentException("Illegal turn")
    }
}).let { stats ->
    Result(first::class.simpleName.orThrow(), second, stats.percentage(second), stats.map { it.moves }.average())
}

private fun benchmark(
    matches: Int = 100,
    players: () -> Pair<TablutClient, TablutClient>
): Collection<Stats> = (1..matches).map {
    debug("MATCH $it:")
    singleMatch(players, matches == 1)
}

private fun singleMatch(
    players: () -> Pair<TablutClient, TablutClient>,
    gui: Boolean = false
): Stats = runBlocking {
    withContext(Dispatchers.Default) {
        async {
            SmartServer(enableGui = gui).apply {
                debug("Start server...")
                run()
                debug("Stop server.")
            }
        }.also {
            delay(1000)
            players().toList().map {
                debug("Start ${it.player}... ")
                launch { it.run() }
            }.forEach {
                it.join()
            }
            debug("Stop players.")
        }.await().stats.apply { /* println("Match won by ${winner}\n") */ }
    }
 }

private infix fun TablutClient.vs(opponent: TablutClient): Pair<TablutClient, TablutClient> = this to opponent