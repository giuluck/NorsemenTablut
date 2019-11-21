package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * Heuristic function to evaluate an action to execute in a state of the game.
 */
interface Heuristic {
    /**
     * The method this heuristic evaluates according to.
     */
    fun evaluate(game: TablutGame, state: State, player: Turn = state.turn): Double
}