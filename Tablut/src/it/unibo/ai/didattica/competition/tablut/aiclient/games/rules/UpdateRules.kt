package it.unibo.ai.didattica.competition.tablut.aiclient.games.rules

import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.*
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*

class UpdateRules private constructor() {
    companion object {
        /**
         * A white (black) pawn is captured if after an opponent move
         * either it is in the middle of two black (white) pawns
         * or it is between a black (white) pawn and a building.
         */
        fun simpleCheckerCapture(citadels: Set<Coord>): UpdateRule =
            BasicUpdateRule { movedPawnCoord ->
                movedPawnCoord.coordsAround(2)
                    .asSequence()
                    .filter { it.checkValidity(9) } // TODO: magic numbers refactoring
                    .filter { aroundCoord ->
                        turn.pawns.contains(pawnAt(aroundCoord))
                                || citadels.contains(aroundCoord)
                                || center == aroundCoord
                    }.map { mateCoord ->
                        movedPawnCoord.coordsBetween(mateCoord).single()
                    }.filter {
                        !citadels.contains(it)
                    }.filter { middleCoord ->
                        when (pawnAt(middleCoord)) {
                            Pawn.BLACK -> turn == Turn.WHITE
                            Pawn.WHITE -> turn == Turn.BLACK
                            Pawn.KING -> turn == Turn.BLACK && center != middleCoord
                                    && !center.coordsAround(1).contains(middleCoord)
                            else -> false
                        }
                    }.forEach { opponentCoord ->
                        if (pawnAt(opponentCoord) == Pawn.KING) turn = Turn.BLACKWIN
                        else board[opponentCoord.x][opponentCoord.y] = Pawn.EMPTY
                    }
            }

        /**
         * The king must be captured by four black pawns if he is inside the tower
         * or by three black pawns if he is adjacent to it.
         */
        fun specialKingCapture(): UpdateRule =
            BasicUpdateRule { movedPawnCoord ->
                if (turn == Turn.BLACK) {
                    movedPawnCoord.coordsAround(1)
                            .filter { it.checkValidity(9) } // TODO: magic numbers refactoring
                            .singleOrNull { aroundCoord -> pawnAt(aroundCoord) == Pawn.KING }
                            ?.coordsAround(1)
                            ?.filter { it.checkValidity(9) } // TODO: magic numbers refactoring and check if necessary
                            ?.all { kingNeighborhood -> kingNeighborhood == center || pawnAt(kingNeighborhood) == Pawn.BLACK }
                            ?.let { kingCaptured -> if (kingCaptured) turn = Turn.BLACKWIN }
                }
            }

        /**
         * If the king reaches one of the winning cells the game ends.
         */
        fun kingEscape(winningCells: Set<Coord>): UpdateRule =
            BasicUpdateRule { movedPawnCoord ->
                if (pawnAt(movedPawnCoord) == Pawn.KING && winningCells.contains(movedPawnCoord)) {
                    turn = Turn.WHITEWIN
                }
            }

        /**
         * If the same state is reached twice, the game ends with a draw.
         */
        fun noDuplicates(): UpdateRule =
            object : UpdateRule {
                private var lastCheckersNumber: Int = Int.MAX_VALUE
                private val previousStates: MutableSet<State> = mutableSetOf()

                override fun update(state: State, action: Action): Unit = with(state) {
                    allCoords.map { state.pawnAt(it) }
                            .filter { it == Pawn.BLACK || it == Pawn.WHITE }
                            .takeIf { it.count() != lastCheckersNumber }
                            ?.let {
                                if (previousStates.contains(this)) turn = Turn.DRAW
                                else previousStates.add(this)
                            } ?: previousStates.clear()
                }
            }
    }
}