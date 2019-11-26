package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics;

import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Coord;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Direction;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.HashMap;
import java.util.Map;

import static it.unibo.ai.didattica.competition.tablut.aiclient.games.board.StateUtilsKt.*;

/**
 * An offline heuristic to evaluate the position of the king in the board.
 * In some boards like the Ashton one, some positions lead to multiple winning cells,
 * the heuristic wants to maximize or minimize the number of winning cells that are reachable.
 */
public class KingStrategicPosition implements Heuristic {

    private static final int MAXIMUM_ESCAPES = Direction.values().length;

    private final Map<Coord, Double> heatMap = new HashMap<>();

    @Override
    public double evaluate(final TablutGame game, final State state, final State.Turn player) {
        for (final Coord c : getAllCoords(state)) {
            heatMap.putIfAbsent(c, (double) game.getWinningCells().stream().filter(wc -> wc.sameColumn(c) || wc.sameRow(c)).count());
        }
        if (game.isTerminal(state)) {
            return game.getUtility(state, player);
        } else {
            Coord kingPosition = getKingCoord(state);
            if(player == State.Turn.WHITE) {
                return (this.heatMap.get(kingPosition)) / KingStrategicPosition.MAXIMUM_ESCAPES;
            } else {
                return -(KingStrategicPosition.MAXIMUM_ESCAPES - this.heatMap.get(kingPosition)) / KingStrategicPosition.MAXIMUM_ESCAPES;
            }
        }
    }
}
