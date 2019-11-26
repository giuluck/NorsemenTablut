package it.unibo.ai.didattica.competition.tablut.aiclient.game

import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.*
import it.unibo.ai.didattica.competition.tablut.aiclient.game.rules.MovementRule
import it.unibo.ai.didattica.competition.tablut.aiclient.game.rules.MovementRules
import it.unibo.ai.didattica.competition.tablut.aiclient.game.rules.UpdateRule
import it.unibo.ai.didattica.competition.tablut.aiclient.game.rules.UpdateRules
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut

/**
 * Implementation of the Ashton version of Tablut using AIMA.
 */
class AshtonTablut : TablutGame {

    private val initialState: StateTablut = StateTablut()

    override val citadels: Set<Coord> = initialState.citadels()

    override val winningCells: Set<Coord> = initialState.winningCells()

    override val movementRules: Collection<MovementRule> = listOf(
        MovementRules.noMovement(),
        MovementRules.outOfBoard(),
        MovementRules.diagonalMovement(),
        MovementRules.pawnClimbing(),
        MovementRules.citadelClimbing(citadels)
    )

    override val updateRules: Collection<UpdateRule> = listOf(
        UpdateRules.stalemate(),
        UpdateRules.simpleCheckerCapture(citadels),
        UpdateRules.specialKingCapture(),
        UpdateRules.kingEscape(winningCells),
        UpdateRules.noDuplicateState()
    )

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