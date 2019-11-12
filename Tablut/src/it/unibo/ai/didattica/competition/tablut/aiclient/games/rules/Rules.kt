package it.unibo.ai.didattica.competition.tablut.aiclient.games.rules

import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Coord
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.center
import it.unibo.ai.didattica.competition.tablut.domain.State.*

/**
 * Static class containing some useful game rules.
 */
class Rules private constructor() {
    companion object {
        /**
         * An action must be written using compulsorily two letters for each coordinate.
         */
        val ACTION_FORMAT = BasicRule {
            from.length == 2 && to.length == 2
        }
        /**
         * An action must be performed only by the player who is currently playing.
         */
        val CORRECT_TURN = BasicRule { state ->
            state.getPawn(rowFrom, columnFrom).let { pawn ->
                (state.turn == Turn.WHITE && (pawn == Pawn.WHITE || pawn == Pawn.KING))
                        || (state.turn == Turn.BLACK && pawn == Pawn.BLACK)
            }
        }
        /**
         * At least one pawn must change its current position.
         */
        val NO_MOVEMENT = BasicRule {
            rowFrom != rowTo || columnFrom != columnTo
        }
        /**
         * A pawn cannot be moved outside the game board.
         */
        val OUT_OF_BOARD = BasicRule { state ->
            Coord(rowFrom, columnFrom).checkValidity(state)
            Coord(rowTo, columnTo).checkValidity(state)
        }
        /**
         * A pawn can be moved either vertically or horizontally but not both.
         */
        val DIAGONAL_MOVEMENT = BasicRule {
            rowFrom == rowTo || columnFrom == columnTo
        }
        /**
         * A pawn cannot climb neither another one nor the throne during its movement.
         */
        val CLIMBING = BasicRule { state ->
            Coord(rowTo, columnTo).coordsUntil(Coord(rowFrom, columnFrom)).all { coord ->
                state.getPawn(coord.x, coord.y) == Pawn.EMPTY
            }
        }
        /**
         * A pawn cannot occupy or climb a citadel.
         */
        val ASHTON_CITADELS = BasicRule { state ->
            ashtonCitadels.getOrPut(state.board.size) {
                state.center.coordsAround(state.board.size / 2)
                        .flatMap { setOf(it, *it.coordsAround().toTypedArray()) }
                        .filter { it.checkValidity(state) }
            }.let { citadels ->
                val from = Coord(rowFrom, columnFrom)
                val to = Coord(rowTo, columnTo)
                if (citadels.contains(from))
                    !citadels.contains(to) || from.distanceTo(to) <= 2
                else
                    to.coordsUntil(from).none { citadels.contains(it) }
            }
        }

        private val ashtonCitadels: MutableMap<Int, List<Coord>> = mutableMapOf()
    }
}