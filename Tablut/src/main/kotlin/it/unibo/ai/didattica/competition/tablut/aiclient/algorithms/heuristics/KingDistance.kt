package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

import it.unibo.ai.didattica.competition.tablut.aiclient.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.Coord
import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.allCoords
import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.center
import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.kingCoord
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.util.orThrow

/**
 * An heuristic based on the minimum Manhattan distance to escape cells.
 */
class KingDistance : Heuristic {
    lateinit var heatMap: Map<Coord, Double>

    override fun evaluate(game: TablutGame, state: State, player: State.Turn): Double = with(state) {
        if (!::heatMap.isInitialized) initialize(game)
        heatMap[kingCoord].orThrow()
    }

    private fun State.initialize(game: TablutGame) {
        val winningCells = game.winningCells
        val maxDistance = winningCells.map { it.distanceTo(center) }.min().orThrow()
        heatMap = allCoords.map { coord -> coord to game.winningCells.map { winningCell -> winningCell.distanceTo(coord) }.min().orThrow() }
            .map { (coord, distance) -> coord to 1.0 - 2.0 * distance / maxDistance }
            .toMap()
    }
}
