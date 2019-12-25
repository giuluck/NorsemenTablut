package it.unibo.ai.didattica.competition.tablut.simulation

import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn
import it.unibo.ai.didattica.competition.tablut.simulation.Simulation.Stats
import it.unibo.ai.didattica.competition.tablut.util.toFile
import java.time.LocalDateTime
import kotlin.math.ceil

enum class Outcome { WIN, DRAW, LOSE }

/**
 * Print generated statistics inside a file.
 */
fun Collection<Stats>.saveToFile() {
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
 * Associate to all the players a list of triples representing the result of each match they played.
 */
val Collection<Stats>.results: Map<TablutClient, List<Triple<TablutClient, Outcome, Int>>>
    get() = flatMap { stats -> with(stats) {
        val (whiteResult, blackResult) = when(result) {
            Turn.WHITEWIN -> Outcome.WIN to Outcome.LOSE
            Turn.BLACKWIN -> Outcome.LOSE to Outcome.WIN
            else -> Outcome.DRAW to Outcome.DRAW
        }
        listOf(
            white to Triple(black, whiteResult, moves),
            black to Triple(white, blackResult, moves)
        )
    } }.groupBy { (player, _) -> player }.mapValues { (_, results) -> results.map { it.second } }

/**
 * Flats the results returning a map that associates to each outcome the average number of moves performed.
 */
fun Collection<Stats>.flatResults(vararg players: TablutClient): Map<Outcome, Double> = with(results) {
    players.flatMap { player -> getValue(player) }
        .map { result -> result.second to result.third }
        .groupBy { (outcome, _) -> outcome }
        .mapValues { (_, moves) -> moves.map { it.second }.average() }
}

/**
 * Associate all the players involved in at least one match
 * with a triple representing their number of wins, draws and loses.
 */
fun Collection<Stats>.endings(
    movesWeights: (Int) -> Triple<Double, Double, Double> = { Triple(1.0, 1.0, 1.0) }
) : Map<TablutClient, Triple<Double, Double, Double>> = analyze(Triple(0.0, 0.0, 0.0)) {
    val weights = movesWeights(it.moves)
    when(it.result) {
        Turn.WHITEWIN -> {
            computeIfPresent(it.white) { _, end -> Triple(end.first + weights.first, end.second, end.third) }
            computeIfPresent(it.black) { _, end -> Triple(end.first, end.second, end.third + weights.third) }
        }
        Turn.BLACKWIN -> {
            computeIfPresent(it.white) { _, end -> Triple(end.first, end.second, end.third + weights.third) }
            computeIfPresent(it.black) { _, end -> Triple(end.first + weights.first, end.second, end.third) }
        }
        else -> {
            computeIfPresent(it.white) { _, end -> Triple(end.first, end.second + weights.second, end.third) }
            computeIfPresent(it.black) { _, end -> Triple(end.first, end.second + weights.second, end.third) }
        }
    }
}

/**
 * Associate all the players involved in at least one match
 * with the number of matches they played.
 */
fun Collection<Stats>.matches(): Map<TablutClient, Int> = analyze(0) {
    computeIfPresent(it.white) { _, matches -> matches + 1 }
    computeIfPresent(it.black) { _, matches -> matches + 1 }
}

/**
 * Associate all the players involved in at least one match
 * with the total number of moves they made.
 */
fun Collection<Stats>.totalMoves(): Map<TablutClient, Int> = analyze(0) {
    computeIfPresent(it.white) { _, matches -> matches + ceil(it.moves / 2.0).toInt() }
    computeIfPresent(it.black) { _, matches -> matches + ceil(it.moves / 2.0).toInt() }
}

/**
 * Associate all the players involved in at least one match
 * with the average number of moves they made.
 */
fun Collection<Stats>.averageMoves(): Map<TablutClient, Double> = with(matches()) {
    totalMoves().toMutableMap().mapValues { it.value.toDouble() / getValue(it.key) }
}

/**
 * Don't consider if a player is black or white and instead aggregate according to its name.
 */
fun <T> Map<TablutClient, T>.withoutRole(operation: List<T>.() -> T): Map<String, T> =
    entries.groupBy { it.key.name }
        .mapValues { (_, valueList) -> valueList.map { it.value }.operation() }

private fun Collection<Stats>.allPlayers(): Set<TablutClient> = map { it.white } union map { it.black }

private fun <T> Collection<Stats>.analyze(initial: T, computation: MutableMap<TablutClient, T>.(Stats) -> Unit): Map<TablutClient, T> =
    allPlayers().map { it to initial }.toMap().toMutableMap().also { map ->
        forEach { map.computation(it) }
    }