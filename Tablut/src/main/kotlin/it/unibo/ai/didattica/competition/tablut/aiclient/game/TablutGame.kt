package it.unibo.ai.didattica.competition.tablut.aiclient.game

import aima.core.search.adversarial.Game
import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.Coord
import it.unibo.ai.didattica.competition.tablut.aiclient.game.rules.MovementRule
import it.unibo.ai.didattica.competition.tablut.aiclient.game.rules.UpdateRule
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn

/**
 * Interface representing the Tablut game using AIMA library.
 */
interface TablutGame : Game<State, Action, Turn> {
    /**
     * The cells of the board where black pawns are positioned at the beginning of the game.
     */
    val citadels: Collection<Coord>

    /**
     * The cells of the game board the king must reach in order to let the white player win.
     */
    val winningCells: Collection<Coord>

    /**
     * The rules according to pawns can be moved inside the game board.
     */
    val movementRules: Collection<MovementRule>

    /**
     * The rules according to the state of the game is updated.
     */
    val updateRules: Collection<UpdateRule>
}