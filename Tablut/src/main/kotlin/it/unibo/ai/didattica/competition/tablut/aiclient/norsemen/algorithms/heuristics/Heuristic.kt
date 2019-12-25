package it.unibo.ai.didattica.competition.tablut.aiclient.norsemen.algorithms.heuristics

import it.unibo.ai.didattica.competition.tablut.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.game.board.opponent
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn

/**
 * Heuristic function to evaluate an action to execute in a state of the game.
 */
interface Heuristic {
    /**
     * The method this heuristic evaluates according to.
     */
    fun evaluate(game: TablutGame, state: State, player: Turn = state.turn.opponent): Double
}