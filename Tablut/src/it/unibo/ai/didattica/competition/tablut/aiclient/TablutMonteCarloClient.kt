package it.unibo.ai.didattica.competition.tablut.aiclient

import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.MonteCarloTreeSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutPlayer
import it.unibo.ai.didattica.competition.tablut.domain.*

/**
 * A Tablut intelligent client using Monte Carlo tree search as the resolution strategy.
 */
class TablutMonteCarloClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutIntelligentClient(
    player,
    timeout,
    ipAddress,
    MonteCarloTreeSearch<State, Action, TablutPlayer>(AshtonTablut(), 5) // TBD
)

fun main() {
    TablutMonteCarloClient("white").run()
}