package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics;

import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Coord;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import static it.unibo.ai.didattica.competition.tablut.aiclient.games.board.StateUtilsKt.getKingCoord;

/**
 * An heuristic based on the minimum Manhattan distance to escape cells.
 */
public class KingDistance implements Heuristic {

    private static final int MAXIMUM_DISTANCE_TO_WINNING_CELL = 7;

    @Override
    public double evaluate(final TablutGame game, final State state, final State.Turn player) {
        if (game.isTerminal(state)) {
            return game.getUtility(state, player);
        } else {
            Coord kingPosition = getKingCoord(state);
            double result = Double.POSITIVE_INFINITY;
            for(Coord c : game.getWinningCells()) {
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
