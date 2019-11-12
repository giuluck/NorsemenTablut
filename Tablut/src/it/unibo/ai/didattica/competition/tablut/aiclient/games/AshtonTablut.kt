package it.unibo.ai.didattica.competition.tablut.aiclient.games

import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.isValidMove
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.Rule
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.Rules
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut
import java.lang.IllegalStateException

/**
 * Implementation of the Ashton version of Tablut using AIMA.
 */
class AshtonTablut : TablutGame {

    override val rules: Set<Rule> = setOf(
        Rules.NO_MOVEMENT,
        Rules.OUT_OF_BOARD,
        Rules.DIAGONAL_MOVEMENT,
        Rules.CLIMBING,
        Rules.ASHTON_CITADELS
    )

    override fun checkMove(state: State, action: Action): State? =
        if (state.isValidMove(action, rules))
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        else null

    override fun getInitialState(): State = StateTablut()

    override fun getResult(state: State, action: Action): State =
        checkMove(state, action) ?: throw IllegalStateException("Invalid Action")

    override fun getPlayer(state: State): TablutPlayer = TablutPlayer.fromTurn(state.turn)

    override fun getPlayers(): Array<TablutPlayer> = TablutPlayer.values()

    override fun getActions(state: State): MutableList<Action> =
            state.allLegalMoves(rules).toMutableList()

    override fun getUtility(state: State, player: TablutPlayer): Double = when(state.turn) {
        Turn.WHITEWIN -> if (player == TablutPlayer.WHITE) 1.0 else -1.0
        Turn.BLACKWIN -> if (player == TablutPlayer.BLACK) 1.0 else -1.0
        Turn.DRAW -> 0.0
        else -> TODO()
    }

    override fun isTerminal(state: State): Boolean =
        listOf(Turn.WHITEWIN, Turn.BLACKWIN, Turn.DRAW).contains(state.turn)
}