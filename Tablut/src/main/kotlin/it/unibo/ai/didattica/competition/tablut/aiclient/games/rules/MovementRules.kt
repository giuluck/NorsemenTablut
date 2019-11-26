package it.unibo.ai.didattica.competition.tablut.aiclient.games.rules

import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Coord
import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * Static class containing some useful game rules.
 */
class MovementRules private constructor() {
    companion object {
        /**
         * An action must be written using compulsorily two letters for each coordinate.
         */
        fun actionFormat(): MovementRule =
            BasicMovementRule { from.length == 2 && to.length == 2 }

        /**
         * An action must be performed only by the player who is currently playing.
         */
        fun correctTurn(): MovementRule =
            BasicMovementRule { state ->
                state.getPawn(rowFrom, columnFrom).let { pawn ->
                    (state.turn == Turn.WHITE && (pawn == Pawn.WHITE || pawn == Pawn.KING))
                        || (state.turn == Turn.BLACK && pawn == Pawn.BLACK)
                }
            }

        /**
         * At least one pawn must change its current position.
         */
        fun noMovement(): MovementRule =
            BasicMovementRule { rowFrom != rowTo || columnFrom != columnTo }

        /**
         * A pawn cannot be moved outside the game board.
         */
        fun outOfBoard(): MovementRule =
            BasicMovementRule { state ->
                Coord(rowFrom, columnFrom).checkValidity(state)
                Coord(rowTo, columnTo).checkValidity(state)
            }

        /**
         * A pawn can be moved either vertically or horizontally but not both.
         */
        fun diagonalMovement(): MovementRule =
            BasicMovementRule { rowFrom == rowTo || columnFrom == columnTo }

        /**
         * A pawn cannot climb neither another one nor the throne during its movement.
         */
        fun pawnClimbing(): MovementRule =
            BasicMovementRule { state ->
                Coord(rowFrom, columnFrom).coordsReaching(Coord(rowTo, columnTo)).all { (x, y) ->
                    state.getPawn(x, y) == Pawn.EMPTY
                }
            }

        /**
         * A pawn cannot occupy or climb a citadel.
         */
        fun citadelClimbing(citadels: Set<Coord>) = BasicMovementRule {
            citadels.let { citadels ->
                val from = Coord(rowFrom, columnFrom)
                val to = Coord(rowTo, columnTo)
                if (citadels.contains(from))
                    !citadels.contains(to) || from.distanceTo(to) <= 2
                else
                    from.coordsReaching(to).none { citadels.contains(it) }
            }
        }
    }
}