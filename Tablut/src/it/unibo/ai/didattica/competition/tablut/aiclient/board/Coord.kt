package it.unibo.ai.didattica.competition.tablut.aiclient.board

import kotlin.math.abs

data class Coord(val x: Int, val y: Int) {
    /**
     * Manhattan distance between two coordinates.
     */
    fun distanceTo(c: Coord): Int = abs(this.x - c.x) + abs(this.y - c.y)

    /**
     * Exclusive list of all the coordinates between this and c.
     *
     * It is assumed that the two coordinates are either in the same row or in the same column,
     * otherwise an empty list will be returned.
     */
    fun coordsUntil(c: Coord): List<Coord> = when {
        this.x == c.x && this.y != c.y ->
            (1 until this.distanceTo(c)).map { Coord(
                this.x,
                this.y + if (this.y > c.y) -it else it
            )}
        this.x != c.x && this.y == c.y ->
            (1 until this.distanceTo(c)).map { Coord(
                this.x + if (this.x > c.x) -it else it,
                this.y
            )}
        else -> listOf()
    }

    override fun toString(): String = (x + 97).toChar() + (y + 1).toString()
}