package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Coord;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.StateUtilsKt;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

/**
 * An heuristic based on the minimum Manhattan distance to escape cells.
 */
public class KingDistance implements Heuristic {
    private static final int MAXIMUM_DISTANCE_TO_WINNING_CELL = 7;
    @Override
    public double evaluate(final Game<State, Action, State.Turn> game, final State state, final State.Turn player) {
        if (game.isTerminal(state)) {
            return game.getUtility(state, player);
        } else {
            Coord kingPosition = new Coord(0,0);
            for(Coord c : StateUtilsKt.playerCoords(state, player)) {
                if(StateUtilsKt.pawnAt(state, c) == State.Pawn.KING) {
                    kingPosition = c;
                    break;
                }
            }

            // TODO: Possible enhancements: Consider enemies (on exit nodes and/or the path)
            double result = Double.POSITIVE_INFINITY;
            for(Coord c : AshtonTablut.Companion.winningCells(state)) {
                if(state.getPawn(c.getX(), c.getY()) != State.Pawn.BLACK) {
                    double distance = c.distanceTo(kingPosition);
                    if(distance < result) {
                        result = distance;
                    }
                }
            }

            if(player == State.Turn.WHITE) {
                return (KingDistance.MAXIMUM_DISTANCE_TO_WINNING_CELL - result) / KingDistance.MAXIMUM_DISTANCE_TO_WINNING_CELL;
            } else {
                return -(result / KingDistance.MAXIMUM_DISTANCE_TO_WINNING_CELL);
            }
        }
    }
}
