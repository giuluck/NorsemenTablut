package it.unibo.ai.didattica.competition.tablut.aiclient.games

import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.Rule
import it.unibo.ai.didattica.competition.tablut.domain.Game
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.Action

/**
 * Interface representing the Tablut game inside AIMA library.
 */
interface TablutGame : Game, aima.core.search.adversarial.Game<State, Action, TablutPlayer> {
    /**
     * The set of rules of this game.
     */
    val rules: Set<Rule>
}

/**
 * Enum class representing the two players of the Tablut game plus one "player" representing an ending state.
 */
enum class TablutPlayer {
    WHITE,
    BLACK,
    NONE;

    companion object {
        fun fromTurn(turn: Turn) = when (turn) {
            Turn.WHITE -> WHITE
            Turn.BLACK -> BLACK
            else -> NONE
        }
    }
}