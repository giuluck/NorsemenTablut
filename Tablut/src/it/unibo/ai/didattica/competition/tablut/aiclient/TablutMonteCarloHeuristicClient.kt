package it.unibo.ai.didattica.competition.tablut.aiclient

import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.TablutMonteCarloHeuristicTreeSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.Heuristic
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.PawnsDifference
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut

/**
 * A Tablut intelligent client using Monte Carlo tree search as the resolution strategy.
 */
open class TablutMonteCarloHeuristicClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost",
    heuristic: Heuristic = PawnsDifference()
) : TablutIntelligentClient(
    player,
    timeout,
    ipAddress,
    TablutMonteCarloHeuristicTreeSearch(AshtonTablut(), (timeout - 5) * 1000L, Int.MAX_VALUE, heuristic)
)

fun main() {
    TablutMonteCarloHeuristicClient("white").run()
}