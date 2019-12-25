package it.unibo.ai.didattica.competition.tablut.simulation

import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.State.*

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
    fun benchmark(players: Pair<TablutClient, TablutClient>, matches: Int): Collection<Stats> =
        (1..matches).map { singleMatch(players, false) }

    /**
     * Execute all possible matches between a given collection of clients considering their role.
     */
    fun championship(players: Collection<TablutClient>): Collection<Stats> =
        players.partition { it.player == Turn.WHITE }.let {
            (whitePlayers, blackPlayers) -> whitePlayers.flatMap { white ->
                blackPlayers.filter { black -> black.name != white.name }.map { black -> white vs black }
            }.map { singleMatch(it, false) }
        }

    data class Stats(
        val white: TablutClient,
        val black: TablutClient,
        val result: Turn,
        val moves: Int
    ) {
        override fun toString(): String = "${white.name} vs ${black.name} ended with a $result in $moves moves."
    }
}

/**
 * An expressive way to create a pair of clients one against the other.
 */
infix fun TablutClient.vs(opponent: TablutClient): Pair<TablutClient, TablutClient> = this to opponent