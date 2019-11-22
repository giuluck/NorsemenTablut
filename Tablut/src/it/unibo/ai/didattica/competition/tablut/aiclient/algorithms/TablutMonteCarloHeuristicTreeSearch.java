package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms;

import aima.core.search.adversarial.AdversarialSearch;
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.Heuristic;
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.tree.Node;
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.tree.NodeFactory;
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Random;

/**
 * Hybrid version between Monte Carlo and Minimax approaches.
 */
public class TablutMonteCarloHeuristicTreeSearch extends MonteCarloTreeSearch<State, Action, State.Turn> implements AdversarialSearch<State, Action> {

    private AdversarialSearch<State, Action> searchStrategy;

    /**
     * @param game           the game model
     * @param executionLimit the maximum  execution time in milliseconds
     * @param iterationLimit the maximum number of iterations
     */
    public TablutMonteCarloHeuristicTreeSearch(AshtonTablut game, long executionLimit, int iterationLimit, Heuristic heuristic) {
        super(game, executionLimit, iterationLimit);
        this.searchStrategy = new TablutIterativeDeepeningAlphaBetaSearch(heuristic, (int) (executionLimit / 100000) );
    }

    @Override
    protected boolean simulate(Node<State, Action> node) {
        // TODO: Use [searchStrategy] here.
        while (!this.game.isTerminal(node.getState())) {
            final Action a = this.searchStrategy.makeDecision(node.getState());
            final State result = this.game.getResult(node.getState(), a);
            final NodeFactory nodeFactory = new NodeFactory();
            node = nodeFactory.createNode(result);
        }

        // TODO: Tie?
        if (this.game.getUtility(node.getState(), this.game.getPlayer(this.tree.getRoot().getState())) > 0) {
            return true;
        } else {
            return false;
        }
    }
}
