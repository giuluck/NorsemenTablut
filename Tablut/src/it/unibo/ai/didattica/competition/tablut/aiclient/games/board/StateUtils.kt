package it.unibo.ai.didattica.competition.tablut.aiclient.games.board

import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.MovementRule
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut
import kotlin.math.abs

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
val State.allCoords: Collection<Coord>
    get() = board.indices.asSequence().flatMap { i ->
        board.indices.map { j -> Coord(i, j) }.asSequence()
    }.toList()

/**
 * A list of all the possible actions (each of them, not just the legal ones) in a state.
 */
val State.allMoves: Collection<Action>
    get() = allCoords.flatMap { start ->
        allCoords.map { end -> Action(start.toString(), end.toString(), turn) }
    }

/**
 * The cells of the board where black pawns are positioned at the beginning of the game.
 */
fun StateTablut.citadels(): Set<Coord> = center
        .coordsAround(size / 2, this)
        .flatMap { setOf(it, *it.coordsAround(1, this).toTypedArray()) }
        .toSet()

/**
 * The cells of the game board the king must reach in order to let white player win.
 */
fun StateTablut.winningCells(): Set<Coord> = allCoords
        .filter { (i, j) -> i == 0 || i == size - 1 || j == 0 || j == size - 1 }
        .filter { (i, j) -> abs(i - j) != 0 && abs(i - j) != size - 1 }
        .filter { coord -> !citadels().contains(coord) }
        .toSet()

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
 * The coordinate where there is the king.
 */
val State.kingCoord: Coord
    get() = playerCoords(Turn.WHITE).first { pawnAt(it) == Pawn.KING }

/**
 * Get all legal moves for the passed position in the current board state.
 */
fun State.legalMovesForCoord(coord: Coord, rules: Collection<MovementRule>): Set<Action> =
    Direction.values()
        .flatMap { legalCoordsInDirection(coord, it) }
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
}

/**
 * Get all the existing coordinates starting from a given position and
 * proceeding always in the same direction until a pawn is met or the end of the board is reached.
 */
fun State.legalCoordsInDirection(coord: Coord, dir: Direction): List<Coord> =
    coordsInDirection(coord, dir).asSequence().takeWhile { pawnAt(it) == Pawn.EMPTY }.toList()

/**
 * Check if the action is valid or not considering the game rules and the current game state.
 */
fun State.isValidMove(action: Action, rules: Collection<MovementRule>): Boolean =
    rules.all { it.check(this, action) }