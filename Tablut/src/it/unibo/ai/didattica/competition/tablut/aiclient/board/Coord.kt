package it.unibo.ai.didattica.competition.tablut.aiclient.board

import java.lang.Integer.min
import kotlin.math.abs

data class Coord(val x: Int, val y: Int) {
    /**
     * Manhattan distance between two coordinates.
     */
    fun distanceTo(c: Coord): Int = abs(x - c.x) + abs(y - c.y)

    /**
     * Exclusive list of all the coordinates between this and c.
     *
     * It is assumed that the two coordinates are either in the same row or in the same column,
     * otherwise an empty list will be returned.
     */
    fun coordsUntil(c: Coord): List<Coord> = when {
        sameRow(c) -> betweenRange(y, c.y).map { Coord(x, it) }
        sameColumn(c) -> betweenRange(x, c.x).map { Coord(it, y) }
        else -> listOf()
    }

    /**
     * Check if the this coordinate lies on the same row of c.
     */
    fun sameRow(c: Coord): Boolean = x == c.x

    /**
     * Check if this the coordinates lie on the same column of c.
     */
    fun sameColumn(c: Coord): Boolean = y == c.y

    override fun toString(): String = (x + 97).toChar() + (y + 1).toString()

    private fun betweenRange(a: Int, b: Int): IntRange =
        if (a < b) a + 1 until b else b + 1 until a
}