package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.Coord;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.StateUtilsKt;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An offline heuristic to evaluate the position of the king in the board.
 * In some boards like the Ashton one, some positions lead to multiple winning cells,
 * the heuristic wants to maximize or minimize the number of winning cells that are reachable.
 *
 * */
public class KingStrategicPosition implements Heuristic {

    private final Map<Coord, Double> heatMap;
    private static final int MAXIMUM_ESCAPES = 4;

    public KingStrategicPosition() {
        this.heatMap = new HashMap<>();
        for (final Iterator<Coord> it = StateUtilsKt.getAllCoords(new StateTablut()).iterator(); it.hasNext(); ) {
            Coord c = it.next();
            int intensity = 0;
            for(final Coord wc : AshtonTablut.Companion.winningCells(new StateTablut())) {
                if(wc.sameColumn(c) || wc.sameRow(c)) {
                    intensity++;
                }
            }
            heatMap.put(c, (double) intensity);
        }
    }

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
            if(player == State.Turn.WHITE) {
                return (this.heatMap.get(kingPosition)) / KingStrategicPosition.MAXIMUM_ESCAPES;
            } else {
                return -(KingStrategicPosition.MAXIMUM_ESCAPES - this.heatMap.get(kingPosition)) / KingStrategicPosition.MAXIMUM_ESCAPES;
            }
        }

    }
}
