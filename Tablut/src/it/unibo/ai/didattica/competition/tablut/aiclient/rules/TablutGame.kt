package it.unibo.ai.didattica.competition.tablut.aiclient.rules

import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.Game
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.Action

/**
 * Interface representing the Tablut game inside AIMA library.
 */
interface TablutGame : Game, aima.core.search.adversarial.Game<State, Action, TablutClient>