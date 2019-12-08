package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms

import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.Heuristic
import it.unibo.ai.didattica.competition.tablut.game.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn

/**
 * Iterative deepening adversarial search using an heuristic for the Tablut game.
 */
class TablutIterativeDeepeningAlphaBetaSearch(
    private val heuristic: Heuristic,
    timeout: Int
) : IterativeDeepeningAlphaBetaSearch<State, Action, Turn>(AshtonTablut(), -1.0, 1.0, timeout) {
    override fun eval(state: State, player: Turn): Double = super.eval(state, player).let { superEvaluation ->
        if (game.isTerminal(state)) superEvaluation
        else heuristic.evaluate(game as TablutGame, state, player).let { heuristicEvaluation ->
            if (player == Turn.WHITE) heuristicEvaluation
            else -heuristicEvaluation
        }
    }
}