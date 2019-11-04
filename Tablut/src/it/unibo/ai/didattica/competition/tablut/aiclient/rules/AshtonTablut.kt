package it.unibo.ai.didattica.competition.tablut.aiclient.rules

import it.unibo.ai.didattica.competition.tablut.aiclient.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut

/**
 * Implementation of the Ashton version of Tablut using AIMA.
 */
class AshtonTablut @JvmOverloads constructor(
    private val initialState: State = StateTablut(),
    private val whitePlayer: TablutClient,
    private val blackPlayer: TablutClient
) : GameAshtonTablut(initialState, 0, 0, "logs", whitePlayer.name, blackPlayer.name),
    TablutGame {
    override fun getInitialState(): State = initialState

    override fun getResult(state: State, action: Action): State = checkMove(state, action)

    override fun getPlayer(state: State): TablutClient = when (state.turn) {
        Turn.WHITE -> whitePlayer
        Turn.BLACK -> blackPlayer
        else -> players.random()
    }

    override fun getPlayers(): Array<TablutClient> = arrayOf(whitePlayer, blackPlayer)

    override fun getActions(state: State): MutableList<Action> =
            state.allLegalMoves(this).toMutableList()

    override fun getUtility(state: State, player: TablutClient): Double = when(state.turn) {
        Turn.WHITEWIN -> if (player.player == State.Turn.WHITE) 1.0 else 0.0
        Turn.BLACKWIN -> if (player.player == State.Turn.BLACK) 1.0 else 0.0
        Turn.DRAW -> 0.5
        else -> TODO() // heuristic function to compute winning probability at a certain state
    }

    override fun isTerminal(state: State): Boolean =
        listOf(Turn.WHITEWIN, Turn.BLACKWIN, Turn.DRAW).contains(state.turn)
}