package it.unibo.ai.didattica.competition.tablut.aiclient.rules

import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * A rule for a game.
 */
interface Rule {
    /**
     * Whether an action can be performed on a state according to this rule.
     */
    fun check(state: State, action: Action): Boolean
}

/**
 * Basic implementation of a rule built using a lambda.
 */
class BasicRule(private val checkRoutine: Action.(State) -> Boolean) : Rule {
    override fun check(state: State, action: Action): Boolean = action.checkRoutine(state)
}