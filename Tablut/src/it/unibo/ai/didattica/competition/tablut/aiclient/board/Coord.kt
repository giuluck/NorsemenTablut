package it.unibo.ai.didattica.competition.tablut.aiclient.board

import it.unibo.ai.didattica.competition.tablut.domain.State
import java.lang.IllegalArgumentException
import kotlin.math.abs

/**
 * An (x, y) coordinate inside the game board.
 */
data class Coord(val x: Int, val y: Int) {
    /**
     * Whether this coordinate is inside the specified state.
     */
    fun checkValidity(state: State) =
        x >= 0 && x < state.board.size && y >= 0 && y < state.board.size

    /**
     * Manhattan distance between two coordinates.
     */
    fun distanceTo(c: Coord): Int = abs(x - c.x) + abs(y - c.y)

    /**
     * The list of city-block coordinates at distance step around this one.
     */
    fun coordsAround(step: Int = 1): List<Coord> = listOf(
        Coord(x - step, y),
        Coord(x + step, y),
        Coord(x, y - step),
        Coord(x, y + step)
    )

    /**
     * Exclusive list of all the coordinates between this and c.
     *
     * It is assumed that the two coordinates are either in the same row or in the same column,
     * otherwise an exception will be thrown.
     */
    fun coordsBetween(c: Coord): List<Coord> = when {
        sameRow(c) -> betweenRange(y, c.y).map { Coord(x, it) }
        sameColumn(c) -> betweenRange(x, c.x).map { Coord(it, y) }
        else -> throw IllegalArgumentException("$c is neither on the same row nor on the same column of $this")
    }

    /**
     * Left-inclusive list of all the coordinates between this and c.
     *
     * It is assumed that the two coordinates are either in the same row or in the same column,
     * otherwise an exception will be thrown.
     */
    fun coordsUntil(c: Coord): List<Coord> = listOf(this, *coordsBetween(c).toTypedArray())

    /**
     * Right-inclusive list of all the coordinates between this and c.
     *
     * It is assumed that the two coordinates are either in the same row or in the same column,
     * otherwise an exception will be thrown.
     */
    fun coordsReaching(c: Coord): List<Coord> = listOf(*coordsBetween(c).toTypedArray(), c)

    /**
     * Inclusive list of all the coordinates between this and c.
     *
     * It is assumed that the two coordinates are either in the same row or in the same column,
     * otherwise an exception will be thrown.
     */
    fun coordTo(c: Coord): List<Coord> = listOf(this, *coordsBetween(c).toTypedArray(), c)

    /**
     * Check if the this coordinate lies on the same row of c.
     */
    fun sameRow(c: Coord): Boolean = x == c.x

    /**
     * Check if this coordinate lies on the same column of c.
     */
    fun sameColumn(c: Coord): Boolean = y == c.y

    override fun toString(): String = "${(y + 97).toChar()}${(x + 1)}"

    private fun betweenRange(a: Int, b: Int): IntRange =
        if (a < b) a + 1 until b else b + 1 until a
}