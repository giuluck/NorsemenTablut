package it.unibo.ai.didattica.competition.tablut.aiclient.games.board

import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.Rule
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * Center cell of the game board.
 */
val State.center: Coord
    get() = Coord(board.size / 2, board.size / 2)

/**
 * A list of all the possible cells of the board expressed as a Coord object.
 */
val State.allCoords: List<Coord>
    get() = board.indices.flatMap { i ->
        board.indices.map { j -> Coord(i, j) }
    }

/**
 * A list of all the possible actions (each of them, not just the legal ones) in a state.
 */
val State.allMoves: List<Action>
    get() = allCoords.flatMap { start ->
        allCoords.map { end -> Action(start.toString(), end.toString(), turn) }
    }

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
    Direction.TOP -> coord.coordsReaching(Coord(0, coord.y))
    Direction.DOWN -> coord.coordsReaching(Coord(board.size - 1, coord.y))
    Direction.LEFT -> coord.coordsReaching(Coord(coord.x, 0))
    Direction.RIGHT -> coord.coordsReaching(Coord(coord.x, board.size - 1))
}

/**
 * Check if the action is valid or not considering the game rules and the current game state.
 */
fun State.isValidMove(action: Action, rules: Set<Rule>): Boolean =
    rules.all { it.check(this, action) }