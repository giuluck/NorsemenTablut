package it.unibo.ai.didattica.competition.tablut.simulation

import it.unibo.ai.didattica.competition.tablut.server.SmartServer.*
import it.unibo.ai.didattica.competition.tablut.util.toFile
import java.time.LocalDateTime
import kotlin.math.ceil

/**
 * Print generated statistics inside a file.
 */
fun Result.saveToFile() {
    val endings = endings()
    allPlayers().joinToString("\n") {
        "$it: ${endings.getValue(it)}"
    }.toFile(
    "benchmarks/${LocalDateTime.now().toString()
            .replace("-", "")
            .replace(":", "")
            .replace(".", "")}.txt"
    )
}

/**
 * Associate all the players involved in at least one match
 * with a triple representing their number of wins, draws and loses.
 */
fun Result.endings(): Map<String, Triple<Int, Int, Int>> = analyze(Triple(0, 0, 0)) {
    when(it.result) {
        "white win" -> {
            computeIfPresent(it.whitePlayer) { _, end -> Triple(end.first + 1, end.second, end.third) }
            computeIfPresent(it.blackPlayer) { _, end -> Triple(end.first, end.second, end.third + 1) }
        }
        "black win" -> {
            computeIfPresent(it.whitePlayer) { _, end -> Triple(end.first, end.second, end.third + 1) }
            computeIfPresent(it.blackPlayer) { _, end -> Triple(end.first + 1, end.second, end.third) }
        }
        else -> {
            computeIfPresent(it.whitePlayer) { _, end -> Triple(end.first, end.second + 1, end.third) }
            computeIfPresent(it.blackPlayer) { _, end -> Triple(end.first, end.second + 1, end.third) }
        }
    }
}

/**
 * Associate all the players involved in at least one match
 * with the number of matches they played.
 */
fun Result.matches(): Map<String, Int> = analyze(0) {
    computeIfPresent(it.whitePlayer) { _, matches -> matches + 1 }
    computeIfPresent(it.blackPlayer) { _, matches -> matches + 1 }
}

/**
 * Associate all the players involved in at least one match
 * with the total number of moves they made.
 */
fun Result.totalMoves(): Map<String, Int> = analyze(0) {
    computeIfPresent(it.whitePlayer) { _, matches -> matches + ceil(it.moves / 2.0).toInt() }
    computeIfPresent(it.blackPlayer) { _, matches -> matches + ceil(it.moves / 2.0).toInt() }
}

/**
 * Associate all the players involved in at least one match
 * with the average number of moves they made.
 */
fun Result.averageMoves(): Map<String, Double> = with(matches()) {
    totalMoves().toMutableMap().mapValues { it.value.toDouble() / this.getValue(it.key) }
}

private fun Result.allPlayers(): Set<String> = map { it.whitePlayer } union map { it.blackPlayer }

private fun <T> Result.analyze(initial: T, computation: MutableMap<String, T>.(Stats) -> Unit): Map<String, T> =
    allPlayers().map { it to initial }.toMap().toMutableMap().also { map ->
        forEach { map.computation(it) }
    }