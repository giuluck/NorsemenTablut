package it.unibo.ai.didattica.competition.tablut.aiclient.games

import aima.core.search.adversarial.Game
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.Action

/**
 * Interface representing the Tablut game inside AIMA library.
 */
interface TablutGame : Game<State, Action, TablutPlayer>

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