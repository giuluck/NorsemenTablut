package it.unibo.ai.didattica.competition.tablut.aiclient.board

import it.unibo.ai.didattica.competition.tablut.aiclient.rules.Rule
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * Center cell of the game board.
 */
val State.center: Coord
    get() = Coord(board.size / 2, board.size / 2)

/**
 * Get all legal moves for the player who has to move.
 */
fun State.allLegalMoves(rules: Set<Rule>): List<Action> = playerCoords(turn)
        .asSequence()
        .flatMap { legalMovesForCoord(it, rules).asSequence() }
        .toList()

/**
 * Get the coordinates of all the pawns the current player owns.
 */
fun State.playerCoords(player: Turn): Set<Coord> = when (player) {
    Turn.WHITE -> setOf(Pawn.WHITE, Pawn.KING)
    Turn.BLACK -> setOf(Pawn.BLACK)
    else -> setOf()
}.let { pawns ->
    board.asSequence()
        .mapIndexed { i, row -> row.mapIndexed { j, pawn -> Coord(i, j) to pawn } }
        .flatten()
        .filter { (_, pawn) -> pawns.contains(pawn) }
        .map { (coord, _) -> coord }
}.toSet()

/**
 * Get all legal moves for the passed position in the current board state.
 */
fun State.legalMovesForCoord(coord: Coord, rules: Set<Rule>): List<Action> =
    Direction.values()
        .flatMap { coordsInDirection(coord, it) }
        .asSequence()
        .map { Action(coord.toString(), it.toString(), turn) }
        .filter { isValidMove(it, rules) }
        .toList()

/**
 * Get all the existing coordinates starting from a given position and
 * proceeding always in the same direction until the end of the board is reached.
 */
fun State.coordsInDirection(coord: Coord, dir: Direction): List<Coord> = when (dir) {
    Direction.TOP -> Coord(0, coord.y).coordsUntil(coord)
    Direction.DOWN -> Coord(board.size - 1, coord.y).coordsUntil(coord)
    Direction.LEFT -> Coord(coord.x, 0).coordsUntil(coord)
    Direction.RIGHT -> Coord(coord.x, board.size - 1).coordsUntil(coord)
}

/**
 * Check if the action is valid or not considering the game rules and the current game state.
 *
 * In order not to perform the action but actually only checking its correctness,
 * instead of the original state one copy of it is evaluated.
 */
fun State.isValidMove(action: Action, rules: Set<Rule>): Boolean =
    rules.all { it.check(this, action) }
