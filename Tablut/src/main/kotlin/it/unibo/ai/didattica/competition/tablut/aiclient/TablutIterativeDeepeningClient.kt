package it.unibo.ai.didattica.competition.tablut.aiclient

import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.TablutIterativeDeepeningAlphaBetaSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.*

/**
 * A Tablut intelligent client using a variant of iterative deepening Minimax search with alpha-beta pruning
 * and action ordering as the resolution strategy.
 */
class TablutIterativeDeepeningClient @JvmOverloads constructor(
    player: String,
    name: String,
    timeout: Int = 60,
    ipAddress: String = "localhost",
    heuristic: Heuristic = if (player.toLowerCase() == "white") NorsemenWhiteHeuristic else NorsemenBlackHeuristic
) : TablutIntelligentClient(
    player,
    name,
    timeout,
    ipAddress,
    TablutIterativeDeepeningAlphaBetaSearch(heuristic, timeout - 5)
)