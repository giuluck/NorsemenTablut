package it.unibo.ai.didattica.competition.tablut.aiclient.prototypes

import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.TablutIterativeDeepeningAlphaBetaSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.Heuristic
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.PiecesDifference
import it.unibo.ai.didattica.competition.tablut.aiclient.prototypes.TablutIntelligentClient

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
    TablutIterativeDeepeningAlphaBetaSearch(heuristic, timeout - 5)
)