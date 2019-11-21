package it.unibo.ai.didattica.competition.tablut.aiclient.games.board

import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.MovementRule
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * Size of the board.
 */
val State.size: Int
    get() = board.size

/**
 * Center cell of the game board.
 */
val State.center: Coord
    get() = Coord(board.size / 2, board.size / 2)

/**
 * A list of all the possible cells of the board expressed as a Coord object.
 */
val State.allCoords: Sequence<Coord>
    get() = board.indices.asSequence().flatMap { i ->
        board.indices.map { j -> Coord(i, j) }.asSequence()
    }

/**
 * A list of all the possible actions (each of them, not just the legal ones) in a state.
 */
val State.allMoves: Sequence<Action>
    get() = allCoords.flatMap { start ->
        allCoords.map { end -> Action(start.toString(), end.toString(), turn) }
    }

/**
 * The set of pawn types owned by each player.
 */
val Turn.pawns: Set<Pawn>
    get() = when (this) {
        Turn.WHITE -> setOf(Pawn.WHITE, Pawn.KING)
        Turn.BLACK -> setOf(Pawn.BLACK)
        else -> setOf()
    }

/**
 * The player who plays against this one.
 */
val Turn.opponent: Turn
    get() = when (this) {
        Turn.WHITE -> Turn.BLACK
        Turn.BLACK -> Turn.WHITE
        else -> this
    }

/**
 * Check whether a state is terminal or not.
 */
val Turn.isTerminal: Boolean
    get() = this == Turn.BLACKWIN || this == Turn.WHITEWIN || this == Turn.DRAW

/**
 * The pawn currently in the specified coordinate of the board.
 */
fun State.pawnAt(c: Coord): Pawn = board[c.x][c.y]

/**
 * Get all legal moves for the player who has to move.
 */
fun State.allLegalMoves(rules: Collection<MovementRule>): Set<Action> =
    playerCoords(turn)
        .asSequence()
        .flatMap { legalMovesForCoord(it, rules).asSequence() }
        .toSet()

/**
 * Get the coordinates of all the pawns the current player owns.
 */
fun State.playerCoords(player: Turn): Set<Coord> =
    board.asSequence()
        .mapIndexed { i, row -> row.mapIndexed { j, pawn -> Coord(i, j) to pawn } }
        .flatten()
        .filter { (_, pawn) -> player.pawns.contains(pawn) }
        .map { (coord, _) -> coord }
        .toSet()

/**
 * Get all legal moves for the passed position in the current board state.
 */
fun State.legalMovesForCoord(coord: Coord, rules: Collection<MovementRule>): Set<Action> =
    Direction.values()
        .flatMap { coordsInDirection(coord, it) }
        .asSequence()
        .map { Action(coord.toString(), it.toString(), turn) }
        .filter { isValidMove(it, rules) }
        .toSet()

/**
 * Get all the existing coordinates starting from a given position and
 * proceeding always in the same direction until the end of the board is reached.
 */
fun State.coordsInDirection(coord: Coord, dir: Direction): List<Coord> = when (dir) {
    Direction.TOP -> coord.coordsReaching(Coord(0, coord.y))
    Direction.DOWN -> coord.coordsReaching(Coord(board.size - 1, coord.y))
    Direction.LEFT -> coord.coordsReaching(Coord(coord.x, 0))
    Direction.RIGHT -> coord.coordsReaching(Coord(coord.x, board.size - 1))
}.asSequence().takeWhile { pawnAt(it) == Pawn.EMPTY }.toList()

/**
 * Check if the action is valid or not considering the game rules and the current game state.
 */
fun State.isValidMove(action: Action, rules: Collection<MovementRule>): Boolean =
    rules.all { it.check(this, action) }