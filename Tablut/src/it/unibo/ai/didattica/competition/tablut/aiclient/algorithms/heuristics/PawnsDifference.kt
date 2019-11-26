package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.playerCoords
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn

/**
 * This heuristic function is based on the pieces difference between the two players.
 */
class PawnsDifference : Heuristic {
    override fun evaluate(game: TablutGame, state: State, player: Turn): Double {
        val white = 2 * (state.playerCoords(Turn.WHITE).size - 1)
        val black = state.playerCoords(Turn.BLACK).size
        return 1.0 * (white - black) / (white + black)
    }
}