package it.unibo.ai.didattica.competition.tablut.aiclient.games

import it.unibo.ai.didattica.competition.tablut.aiclient.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.aiclient.rules.Rule
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut
import java.lang.IllegalStateException

/**
 * Implementation of the Ashton version of Tablut using AIMA.
 */
class AshtonTablut @JvmOverloads constructor(
    private val initialState: State = StateTablut()
) : TablutGame {
    override fun checkMove(state: State, action: Action): State? {
        Rule.values().map { it.checkRule(action, state) }
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInitialState(): State = initialState

    override fun getResult(state: State, action: Action): State =
        checkMove(state, action) ?: throw IllegalStateException("Invalid Action")

    override fun getPlayer(state: State): Player = Player.fromTurn(state.turn)

    override fun getPlayers(): Array<Player> = Player.values()

    override fun getActions(state: State): MutableList<Action> =
            state.allLegalMoves(this).toMutableList()

    override fun getUtility(state: State, player: Player): Double = when(state.turn) {
        Turn.WHITEWIN -> if (player == Player.WHITE) 1.0 else -1.0
        Turn.BLACKWIN -> if (player == Player.BLACK) 1.0 else -1.0
        Turn.DRAW -> 0.0
        else -> TODO()
    }

    override fun isTerminal(state: State): Boolean =
        listOf(Turn.WHITEWIN, Turn.BLACKWIN, Turn.DRAW).contains(state.turn)
}