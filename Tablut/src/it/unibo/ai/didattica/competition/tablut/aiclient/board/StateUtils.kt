package it.unibo.ai.didattica.competition.tablut.aiclient.board

import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.Game
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * Get all legal moves for the player who has to move.
 */
fun State.allLegalMoves(rules: Game): List<Action> = this.playerCoords(this.turn)
        .asSequence()
        .flatMap { this.legalMovesForCoord(it, rules).asSequence() }
        .toList()

/**
 * Get the coordinates of all the pawns the current player owns.
 */
fun State.playerCoords(player: State.Turn): Set<Coord> = when (player) {
    State.Turn.WHITE -> setOf(State.Pawn.WHITE, State.Pawn.KING)
    State.Turn.BLACK -> setOf(State.Pawn.BLACK)
    else -> setOf()
}.let {
    val pawnCoords = mutableSetOf<Coord>()
    for (i in 0 until this.board.size - 1) {
        for (j in 0 until this.board[i].size - 1) {
            if (it.contains(this.board[i][j])) {
                pawnCoords.add(Coord(i, j))
            }
        }
    }
    return pawnCoords
}

/**
 * Get all legal moves for the passed position in the current board state.
 */
fun State.legalMovesForCoord(start: Coord, rules: Game): List<Action> = start
        .let { Direction.values().flatMap { dir -> this.coordsInDirection(start, dir) } }
        .asSequence()
        .map { Action(start.toString(), it.toString(), this.turn) }
        .filter { this.isValidMove(it, rules) }
        .toList()

/**
 * Get all the existing coordinates starting from a given position and
 * proceeding always in the same direction until the end of the board is reached.
 */
private fun State.coordsInDirection(start: Coord, dir: Direction): List<Coord> = when (dir) {
    Direction.TOP -> start.coordsUntil(Coord(0, start.y))
    Direction.DOWN -> start.coordsUntil(Coord(this.board.size - 1, start.y))
    Direction.LEFT -> start.coordsUntil(Coord(start.x, 0))
    Direction.RIGHT -> start.coordsUntil(Coord(start.x, this.board[0].size - 1))
}

/**
 * Check if the action is valid or not considering the game rules and the current game state.
 *
 * In order not to perform the action but actually only checking its correctness,
 * instead of the original state one copy of it is evaluated.
 *
 * TODO: The current implementation is particularly resource heavy
 */
private fun State.isValidMove(action: Action, rules: Game): Boolean =
        runCatching { rules.checkMove(this.clone(), action) }.isSuccess