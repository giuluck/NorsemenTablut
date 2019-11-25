package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics;

import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Coord;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Direction;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.StateUtilsKt;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import static it.unibo.ai.didattica.competition.tablut.aiclient.games.board.StateUtilsKt.getKingCoord;

/**
 * An heuristic based on the number of black pawns and obstacles surrounding the king.
 */
public class KingSurrounded implements Heuristic {

    private static final int STEP = 1;

    @Override
    public double evaluate(final TablutGame game, final State state, final State.Turn player) {
        if (game.isTerminal(state)) {
            return game.getUtility(state, player);
        }
        Coord kingPosition = getKingCoord(state);
        double result = 0;
        for (Coord neighbor : kingPosition.coordsAround(KingSurrounded.STEP, state)) {
            State.Pawn neighborPawn = StateUtilsKt.pawnAt(state, neighbor);
            if(neighborPawn == State.Pawn.BLACK || neighborPawn == State.Pawn.THRONE || game.getCitadels().contains(neighbor)) {
                result++;
            }
        }
        if(player == State.Turn.WHITE) {
            return (Direction.values().length - result) / Direction.values().length;
        } else {
            return -(result / Direction.values().length);
        }
    }
}
