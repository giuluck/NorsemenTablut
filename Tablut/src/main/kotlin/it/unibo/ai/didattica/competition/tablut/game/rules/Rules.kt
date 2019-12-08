package it.unibo.ai.didattica.competition.tablut.game.rules

import it.unibo.ai.didattica.competition.tablut.game.board.Coord
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * A generic rule in the game.
 */
interface Rule

/**
 * A movement rule for a game.
 */
interface MovementRule : Rule {
    /**
     * Whether an [action] can be performed on a [state] according to this rule.
     */
    fun check(state: State, action: Action): Boolean
}

/**
 * An update rule for the game.
 */
interface UpdateRule : Rule {
    /**
     * Performs an [action] over a [state].
     */
    fun update(state: State, action: Action)
}

/**
 * Basic implementation of a movement rule built using a lambda.
 */
class BasicMovementRule(private val checkRoutine: Action.(State) -> Boolean) : MovementRule {
    override fun check(state: State, action: Action): Boolean = action.checkRoutine(state)
}

/**
 * Basic implementation of a capture rule built using a lambda.
 */
class BasicUpdateRule(private val updateRoutine: State.(Coord) -> Unit) : UpdateRule {
    override fun update(state: State, action: Action): Unit = with(action) {
        state.updateRoutine(Coord(rowTo, columnTo))
    }
}