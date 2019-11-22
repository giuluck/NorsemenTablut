package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import aima.core.search.framework.Metrics;
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.tree.GameTree;
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.tree.Node;
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.tree.NodeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Artificial Intelligence A Modern Approach (4th Edition): page ???.<br>
 *
 * <pre>
 * <code>
 * function MONTE-CARLO-TREE-SEARCH(state) returns an action
 *   tree &larr; NODE(state)
 *   while TIME-REMAINING() do
 *   	leaf &larr; SELECT(tree)
 *   	child &larr; EXPAND(leaf)
 *   	result &larr; SIMULATE(child)
 *   	BACKPROPAGATE(result, child)
 *   return the move in ACTIONS(state) whose node has highest number of playouts
 * </code>
 * </pre>
 *
 * Figure ?.? The Monte Carlo tree search algorithm. A game tree, tree, is initialized, and
 * then we repeat the cycle of SELECT / EXPAND / SIMULATE/ BACKPROPAGATE until we run  out
 * of time, and return the move that led to the node with the highest number of playouts.
 *
 *
 * @author Suyash Jain
 *
 * @param <S>
 *            Type which is used for states in the game.
 * @param <A>
 *            Type which is used for actions in the game.
 * @param <P>
 *            Type which is used for players in the game.
 */

public class MonteCarloTreeSearch<S, A, P> implements AdversarialSearch<S, A> {
	final private long executionLimit;
	final private int iterationLimit;
	private int currentIteration;

	final protected Game<S, A, P> game;
	protected GameTree<S, A> tree;

	/**
	 *
	 * @param game the game model
	 * @param executionLimit the maximum  execution time in milliseconds
	 * @param iterationLimit the maximum number of iterations
	 */
	public MonteCarloTreeSearch(final Game<S, A, P> game, final long executionLimit, final int iterationLimit) {
		this.game = game;
		this.executionLimit = executionLimit;
		this.iterationLimit = iterationLimit;
		this.currentIteration = 0;
		this.tree = new GameTree<>();
	}
	
	@Override
	public A makeDecision(final S state) {
		// TODO: May exist a better way to reinitialize MCTS
		this.tree = new GameTree<>();
		this.currentIteration = 0;

		// tree <-- NODE(state)
		this.tree.addRoot(state);
		final ExecutorService executor = Executors.newSingleThreadExecutor();

		try {
			final Future<Object> routine = executor.submit(() -> {
				while (this.currentIteration < this.iterationLimit) {
					this.algorithmCore();
					this.currentIteration++;
				}
				return null;
			});

			routine.get(this.executionLimit, TimeUnit.MILLISECONDS);
		} catch (final TimeoutException e) {
			//System.err.println("Timeout");
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			executor.shutdown();
			//System.out.println("Iterations made: " + this.currentIteration);
		}

		// return the move in ACTIONS(state) whose node has highest number of playouts
		return bestAction(this.tree.getRoot());
	}

	private void algorithmCore() {
		// leaf <-- SELECT(tree)
		final Node<S, A> leaf = this.select(this.tree);
		// child <-- EXPAND(leaf)
		final Node<S, A> child = this.expand(leaf);
		// result <-- SIMULATE(child)
		// result = true if player of root node wins
		final boolean result = this.simulate(child);
		// BACKPROPAGATE(result, child)
		this.backpropagate(result, child);
		System.out.println("=============================");
	}

	/*
	POSSIBLE ENHANCEMENTS:
	- MCTS-Solver
	- Node priors
	- RAVE
	*/
	private Node<S, A> select(final GameTree<S, A> gameTree) {
		Node<S, A> node = gameTree.getRoot();
		while (!this.game.isTerminal(node.getState()) && this.isNodeFullyExpanded(node)) {
			node = gameTree.getChildWithMaxUCT(node);
		}
		return node;
	}
	
	private Node<S, A> expand(final Node<S, A> leaf) {
		if (this.game.isTerminal(leaf.getState())) {
			return leaf;
		} else {
			return this.randomlySelectUnvisitedChild(leaf);
		}
	}

	/*
	POSSIBLE ENHANCEMENTS:
	- informed rollout policies
	- adaptive rollout policies
	- rollout cutoffs

	- consider to use a probability to use heuristics or not and consider the state of the match (pieces, opening/closing)
	*/
	protected boolean simulate(Node<S, A> node) {
		while (!this.game.isTerminal(node.getState())) {
			final Random rand = new Random();
			final A a = this.game.getActions(node.getState()).get(rand.nextInt(this.game.getActions(node.getState()).size()));
			final S result = this.game.getResult(node.getState(), a);
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
	
	private void backpropagate(final boolean result, final Node<S, A> node) {
		this.tree.updateStats(result, node);
		//System.out.println(this.tree.getWi().get(node.getState()) + "/" + this.tree.getNi().get(node.getState()));
		if (node.getParent() != null) {
			// TODO: !result
			this.backpropagate(!result, node.getParent());
		}
	}
	
	private A bestAction(final Node<S, A> root) {
		// TODO: To tune, originally is MaxPlayouts
		// final Node<S, A> bestChild = tree.getChildWithMaxUCT(root);
		final Node<S, A> bestChild = tree.getChildWithMaxPlayouts(root);
		for (final A a : this.game.getActions(root.getState())) {
			S result = this.game.getResult(root.getState(), a);
			if (result.equals(bestChild.getState())){
				return a;
			}
		}
		return null;
	}
	
	private boolean isNodeFullyExpanded(final Node<S, A> node) {
		final List<S> visitedChildren = this.tree.getVisitedChildren(node);
		for (final A a : this.game.getActions(node.getState())) {
			final S result = this.game.getResult(node.getState(), a);
			if (!visitedChildren.contains(result)) {
				return false;
			}
		}
		return true;
	}
	
	
	private Node<S, A> randomlySelectUnvisitedChild(final Node<S, A> node) {
		final List<S> unvisitedChildren = new ArrayList<>();
		final List<S> visitedChildren = tree.getVisitedChildren(node);
		final List<A> actions = new ArrayList<>();
		for (final A a : this.game.getActions(node.getState())) {
			final S result = this.game.getResult(node.getState(), a);
			if (!visitedChildren.contains(result)) {
				unvisitedChildren.add(result);
				actions.add(a);
			}
		}
		final int randomIndex = new Random().nextInt(unvisitedChildren.size());
		return this.tree.addChild(node, unvisitedChildren.get(randomIndex), actions.get(randomIndex));
	}
	
	@Override
	public Metrics getMetrics() {
		return null;
	}
}
