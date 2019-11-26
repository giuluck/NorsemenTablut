package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.playerCoords

import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * This heuristic function is based on the pieces difference between the two players.
 */
class PiecesDifference : Heuristic {
    override fun evaluate(game: TablutGame, state: State, player: State.Turn): Double {
        return if (game.isTerminal(state)) {
            game.getUtility(state, player)
        } else {
            val white = 2 * (state.playerCoords(State.Turn.WHITE).size - 1)
            val black = state.playerCoords(State.Turn.BLACK).size
            val sign = if (player == State.Turn.WHITE) 1.0 else -1.0
            sign * (white - black) / (white + black)
        }
    }
}