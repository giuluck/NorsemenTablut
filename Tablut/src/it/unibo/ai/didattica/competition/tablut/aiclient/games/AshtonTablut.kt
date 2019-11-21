package it.unibo.ai.didattica.competition.tablut.aiclient.games

import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.*
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.MovementRule
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.MovementRules
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.UpdateRule
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.UpdateRules
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut
import kotlin.IllegalStateException
import kotlin.math.abs

/**
 * Implementation of the Ashton version of Tablut using AIMA.
 */
class AshtonTablut : TablutGame {
    companion object {
        fun citadels(state: State): Set<Coord> = state.center
            .coordsAround(state.size / 2, state)
            .flatMap { setOf(it, *it.coordsAround(1, state).toTypedArray()) }
            .toSet()

        fun winningCells(state: State): Set<Coord> = state.allCoords
            .filter { (i, j) -> i == 0 || i == state.size - 1 || j == 0 || j == state.size - 1 }
            .filter { (i, j) -> abs(i - j) != 0 && abs(i - j) != state.size - 1 }
            .filter { coord -> !citadels(state).contains(coord) }
            .toSet()

        fun movementRules(state: State): Collection<MovementRule> = listOf(
            MovementRules.noMovement(),
            MovementRules.outOfBoard(),
            MovementRules.diagonalMovement(),
            MovementRules.pawnClimbing(),
            MovementRules.citadelClimbing(citadels(state))
        )

        fun updateRules(state: State): Collection<UpdateRule> = listOf(
            UpdateRules.stalemate(),
            UpdateRules.simpleCheckerCapture(citadels(state)),
            UpdateRules.specialKingCapture(),
            UpdateRules.kingEscape(winningCells(state)),
            UpdateRules.noDuplicateState()
        )
    }

    private val initialState: State = StateTablut()

    private val movementRules: Collection<MovementRule> = movementRules(initialState)

    private val updateRules: Collection<UpdateRule> = updateRules(initialState)

    override fun getInitialState(): State = initialState

    override fun getResult(state: State, action: Action): State = state.clone().apply {
        board[action.rowTo][action.columnTo] = board[action.rowFrom][action.columnFrom]
        board[action.rowFrom][action.columnFrom] = Pawn.EMPTY
        center.takeIf { board[it.x][it.y] == Pawn.EMPTY }?.let { board[it.x][it.y] = Pawn.THRONE }
        updateRules.takeWhile {
            it.update(this, action)
            !state.turn.isTerminal
        }
        turn = turn.opponent
    }

    override fun getPlayer(state: State): Turn = state.turn

    override fun getPlayers(): Array<Turn> = Turn.values()

    override fun getActions(state: State): List<Action> =
        with (state.allLegalMoves(movementRules)) {
            if (isEmpty()) listOf(UpdateRules.stalemateAction(state.turn)) else toList()
        }

    override fun getUtility(state: State, player: Turn): Double = when(state.turn) {
        Turn.WHITEWIN -> if (player == Turn.WHITE) 1.0 else -1.0
        Turn.BLACKWIN -> if (player == Turn.BLACK) 1.0 else -1.0
        Turn.DRAW -> 0.0
        else -> throw IllegalStateException("Not a terminal state")
    }

    override fun isTerminal(state: State): Boolean = state.turn.isTerminal
}