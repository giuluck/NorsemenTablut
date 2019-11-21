package it.unibo.ai.didattica.competition.tablut.aiclient

import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.MonteCarloTreeSearch
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.domain.*
import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * A Tablut intelligent client using Monte Carlo tree search as the resolution strategy.
 */
open class TablutMonteCarloClient @JvmOverloads constructor(
    player: String,
    timeout: Int = 60,
    ipAddress: String = "localhost"
) : TablutIntelligentClient(
    player,
    timeout,
    ipAddress,
    MonteCarloTreeSearch<State, Action, Turn>(AshtonTablut(), (timeout - 5) * 1000L, Int.MAX_VALUE)
)