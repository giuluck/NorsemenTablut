package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.Heuristic;
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.tree.Node;

/**
 * Hybrid version between Monte Carlo and Minimax approaches.
 */
public class MonteCarloHeuristicTreeSearch extends MonteCarloTreeSearch implements AdversarialSearch {

    private AdversarialSearch searchStrategy;

    /**
     * @param game           the game model
     * @param executionLimit the maximum  execution time in milliseconds
     * @param iterationLimit the maximum number of iterations
     */
    public MonteCarloHeuristicTreeSearch(Game game, long executionLimit, int iterationLimit, Heuristic heuristic) {
        super(game, executionLimit, iterationLimit);
        this.searchStrategy = new TablutIterativeDeepeningAlphaBetaSearch(heuristic, (int) (executionLimit / 1000) );
    }

    @Override
    protected boolean simulate(Node node) {
        // TODO: Use [searchStrategy] here.
        return true;
    }
}6
