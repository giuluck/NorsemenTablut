package it.unibo.ai.didattica.competition.tablut.aiclient

import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.Heuristic
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.PiecesDifference
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.domain.*

import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * A Tablut intelligent client using a variant of iterative deepening Minimax search with alpha-beta pruning and
 * action ordering as the resolution strategy.
 */
class TablutIterativeDeepeningClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost",
    heuristic: Heuristic = PiecesDifference()
) : TablutIntelligentClient(
    player,
    timeout,
    ipAddress,
    object : IterativeDeepeningAlphaBetaSearch<State, Action, Turn> (AshtonTablut(), -1.0, 1.0, timeout - 5) {
        override fun eval(state: State, player: Turn): Double {
            super.eval(state, player)
            return heuristic.evaluate(game, state, player)
        }
    }
)