package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

import it.unibo.ai.didattica.competition.tablut.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.game.board.Coord
import it.unibo.ai.didattica.competition.tablut.game.board.Direction
import it.unibo.ai.didattica.competition.tablut.game.board.allCoords
import it.unibo.ai.didattica.competition.tablut.game.board.kingCoord
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * An offline heuristic to evaluate the position of the king in the board.
 * In some boards like the Ashton one, some positions lead to multiple winning cells,
 * the heuristic wants to maximize or minimize the number of winning cells that are reachable.
 */
class KingStrategicPosition : Heuristic {
    lateinit var heatMap: Map<Coord, Double>

    override fun evaluate(game: TablutGame, state: State, player: State.Turn): Double = with(state) {
        if (!::heatMap.isInitialized) initialize(game)
        heatMap.getValue(state.kingCoord)
    }

    private fun State.initialize(game: TablutGame) {
        val maxEscapes = Direction.values().size
        heatMap = allCoords.map { coord -> coord to game.winningCells.filter { winningCell -> winningCell.sameColumn(coord) || winningCell.sameRow(coord) } }
            .map { (coord, cells) -> coord to (2.0 * cells.count() / maxEscapes - 1) }
            .toMap()
    }
}
