package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

import it.unibo.ai.didattica.competition.tablut.aiclient.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.aiclient.game.board.*
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn
import kotlin.math.min

/**
 * An heuristic based on the number of black and white pawns and obstacles surrounding the king on the row and column
 * where is positioned.
 */
class KingSurrounded : Heuristic {
    private lateinit var citadels: Set<Coord>
    private var maxDistance: Int = 0

    override fun evaluate(game: TablutGame, state: State, player: State.Turn): Double = with (state) {
        if (!::citadels.isInitialized) initialize(game)
        val horizontal = weight(Direction.LEFT) + weight(Direction.RIGHT)
        val vertical = weight(Direction.TOP) + weight(Direction.DOWN)
        0.5 * min(horizontal, vertical) / maxDistance
    }

    private fun State.initialize(game: TablutGame) {
        citadels = game.citadels.toSet()
        maxDistance = size - 2
    }

    /**
     * A white checker has weight +1.
     * A black checker has weight -1.
     * The throne has weight +1 iff it's adjacent to the king, otherwise it's like an empty cell.
     * A citadel has weight -1 iff it's adjacent to the king, otherwise it's like an empty cell.
     * A winning cell has weight +1.
     * An empty cell has weight 0.
     */
    private fun State.weight(direction: Direction): Int {
        var weight = maxDistance
        coordsInDirection(kingCoord, direction).forEach { coord ->
            when (pawnAt(coord)) {
                Pawn.WHITE, Pawn.KING -> return weight
                Pawn.BLACK -> return -weight
                Pawn.THRONE -> return if (weight == maxDistance) weight else 0
                Pawn.EMPTY -> if (citadels.contains(coord)) return if (weight == maxDistance) -weight else 0
            }
            weight--
        }
        return 0
    }
}
