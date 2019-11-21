package it.unibo.ai.didattica.competition.tablut.aiclient.games

import aima.core.search.adversarial.Game
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*
import it.unibo.ai.didattica.competition.tablut.domain.Action

/**
 * Interface representing the Tablut game inside AIMA library.
 */
interface TablutGame : Game<State, Action, Turn>