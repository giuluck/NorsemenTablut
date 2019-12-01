package it.unibo.ai.didattica.competition.tablut.simulation

import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.server.SmartServer.*

typealias Result = Collection<Stats>

/**
 * An environment where it is possible to execute a match between two Tablut clients of any kind.
 */
interface Simulation {
    /**
     * Execute a single match between the two specified clients.
     */
    fun singleMatch(players: Pair<TablutClient, TablutClient>, gui: Boolean = true): Stats

    /**
     * Execute a certain number of [matches] between the same two clients.
     */
    fun benchmark(players: Pair<TablutClient, TablutClient>, matches: Int): Result =
        (1..matches).map { singleMatch(players, false) }

    /**
     * Execute all possible matches between a given collection of clients considering their role.
     */
    fun championship(players: Collection<TablutClient>): Result =
        players.partition { it.player == Turn.WHITE }.let {
            (whitePlayers, blackPlayers) -> whitePlayers.flatMap { white ->
                blackPlayers.map { black -> white vs black }
            }.map { singleMatch(it, false) }
        }

    /**
     * An expressive way to create a pair of clients one against the other.
     */
    infix fun TablutClient.vs(opponent: TablutClient): Pair<TablutClient, TablutClient> = this to opponent
}