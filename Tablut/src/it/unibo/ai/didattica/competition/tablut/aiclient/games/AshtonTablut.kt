package it.unibo.ai.didattica.competition.tablut.aiclient.games

import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.*
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.MovementRule
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.MovementRules
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.UpdateRule
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.UpdateRules
import it.unibo.ai.didattica.competition.tablut.aiclient.test.toConsole
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut
import java.lang.IllegalStateException
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

        fun movementRules(state: State): Set<MovementRule> = setOf(
            MovementRules.noMovement(),
            MovementRules.outOfBoard(),
            MovementRules.diagonalMovement(),
            MovementRules.pawnClimbing(),
            MovementRules.citadelClimbing(citadels(state))
        )

        fun updateRules(state: State): Set<UpdateRule> = setOf(
            UpdateRules.simpleCheckerCapture(citadels(state)),
            UpdateRules.specialKingCapture(),
            UpdateRules.kingEscape(winningCells(state)),
            UpdateRules.noDuplicates()
        )
    }

    private val initialState: State = StateTablut()

    private val movementRules: Set<MovementRule> = movementRules(initialState)

    private val updateRules: Set<UpdateRule> = updateRules(initialState)

    override fun getInitialState(): State = initialState

    override fun getResult(state: State, action: Action): State =
        state.clone().apply {
            board[action.rowTo][action.columnTo] = board[action.rowFrom][action.columnFrom]
            board[action.rowFrom][action.columnFrom] = Pawn.EMPTY
            center.takeIf { board[it.x][it.y] == Pawn.EMPTY }?.let { board[it.x][it.y] = Pawn.THRONE }
            updateRules.forEach { it.update(this, action) }
            turn = turn.opponent
        }

    override fun getPlayer(state: State): TablutPlayer = TablutPlayer.fromTurn(state.turn)

    override fun getPlayers(): Array<TablutPlayer> = TablutPlayer.values()

    override fun getActions(state: State): List<Action> =
        state.allLegalMoves(movementRules).toList().apply {
            if (isEmpty()) {
                state.turn = if (state.turn == Turn.WHITE) Turn.BLACKWIN else Turn.WHITEWIN
            }
        }

    override fun getUtility(state: State, player: TablutPlayer): Double = when(state.turn) {
        Turn.WHITEWIN -> if (player == TablutPlayer.WHITE) 1.0 else -1.0
        Turn.BLACKWIN -> if (player == TablutPlayer.BLACK) 1.0 else -1.0
        Turn.DRAW -> 0.0
        else -> throw IllegalStateException("Not a Terminal State")
    }

    override fun isTerminal(state: State): Boolean = state.turn.isTerminal
}