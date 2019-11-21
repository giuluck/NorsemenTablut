package it.unibo.ai.didattica.competition.tablut.aiclient

import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.opponent
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.playerCoords
import it.unibo.ai.didattica.competition.tablut.aiclient.test.toConsole
import it.unibo.ai.didattica.competition.tablut.domain.*

import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * A Tablut intelligent client using a variant of iterative deepening Minimax search with alpha-beta pruning and
 * action ordering as the resolution strategy.
 */
class TablutIterativeDeepeningClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutIntelligentClient(
    player,
    timeout,
    ipAddress,
    object : IterativeDeepeningAlphaBetaSearch<State, Action, Turn> (AshtonTablut(), -1.0, 1.0, timeout - 5) {
        override fun eval(state: State, player: Turn): Double {
            super.eval(state, player)
            return if (game.isTerminal(state)) {
                game.getUtility(state, player);
            } else {
                val white = 2 * (state.playerCoords(Turn.WHITE).size - 1)
                val black = state.playerCoords(Turn.BLACK).size
                val sign = if (player == Turn.WHITE) 1.0 else -1.0
                sign * (white - black) / (white + black)
            }
        }
    }
)

fun main() {
    TablutIterativeDeepeningClient("white").run()
}