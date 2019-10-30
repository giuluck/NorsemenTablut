package it.unibo.ai.didattica.competition.tablut.aiclient

import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.Game
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * Retrieves all successor states for a given board state by applying
 * all legal moves to clones of the current board state.
 */
fun State.getSuccessors(rules: Game): List<State> = this.getAllLegalMoves(rules)
        .map { rules.checkMove(this.clone(), it) }

/**
 * Get all legal moves for the player who has to move.
 */
fun State.getAllLegalMoves(rules: Game): List<Action> = this.getPlayerCoords(this.turn)
        .asSequence()
        .flatMap { this.getLegalMovesForCoord(it, rules) }
        .toList()

/**
 * Get the coordinates of all the pawns the current player owns.
 */
fun State.getPlayerCoords(player: State.Turn): Set<Coord> = when (player) {
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
fun State.getLegalMovesForCoord(start: Coord, rules: Game): Sequence<Action> = start
        .let { Direction.values().flatMap { dir -> this.getCoordsInDirection(start, dir) } }
        .map { Action(start.toString(), it.toString(), this.turn) }
        .asSequence()
        .filter { this.isValidMove(it, rules) }

/**
 * Get all the existing coordinates starting from a given position and
 * proceeding always in the same direction until the end of the board is reached.
 */
private fun State.getCoordsInDirection(start: Coord, dir: Direction): List<Coord> = when (dir) {
    Direction.TOP -> start.getCoordsUntil(Coord(0, start.y))
    Direction.DOWN -> start.getCoordsUntil(Coord(this.board.size - 1, start.y))
    Direction.LEFT -> start.getCoordsUntil(Coord(start.x, 0))
    Direction.RIGHT -> start.getCoordsUntil(Coord(start.x, this.board[0].size - 1))
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