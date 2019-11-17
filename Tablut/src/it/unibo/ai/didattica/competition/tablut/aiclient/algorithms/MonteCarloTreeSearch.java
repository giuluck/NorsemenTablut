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
	private long executionLimit;
	final private boolean isTime;

	final private Game<S, A, P> game;
	final private GameTree<S, A> tree;

	/**
	 *
	 * @param game the game model
	 * @param executionLimit the maximum number of iterations or the maximum  execution time in milliseconds
	 * @param isTime consider <code>executionLimit</code> as time
	 */
	public MonteCarloTreeSearch(final Game<S, A, P> game, final long executionLimit, final boolean isTime) {
		this.game = game;
		this.executionLimit = executionLimit;
		this.isTime = isTime;
		this.tree = new GameTree<>();
	}
	
	@Override
	public A makeDecision(final S state) {
		// tree <-- NODE(state)
		this.tree.addRoot(state);

		if (this.isTime) {
			final ExecutorService executor = Executors.newSingleThreadExecutor();

			try {
				final Future<Object> routine = executor.submit(() -> {
					this.algorithmCore();
					//Thread.sleep(1500);
					return null;
				});

				routine.get(this.executionLimit, TimeUnit.MILLISECONDS);
			} catch (final TimeoutException e) {
				System.err.println("Timeout");
			} catch (final Exception e) {
				throw new RuntimeException(e);
			} finally {
				executor.shutdown();
			}
		} else {
			while (this.executionLimit != 0) {
				this.algorithmCore();
				// repeat the four steps for set number of iterations
				--this.executionLimit;
			}
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
	}
	
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
	
	private boolean simulate(Node<S, A> node) {
		while (!this.game.isTerminal(node.getState())) {
			final Random rand = new Random();
			final A a = this.game.getActions(node.getState()).get(rand.nextInt(this.game.getActions(node.getState()).size()));
			final S result = this.game.getResult(node.getState(), a);
			final NodeFactory nodeFactory = new NodeFactory();
			node = nodeFactory.createNode(result);
		}
		if (this.game.getUtility(node.getState(), this.game.getPlayer(this.tree.getRoot().getState())) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private void backpropagate(final boolean result, final Node<S, A> node) {
		this.tree.updateStats(result, node);
		if (this.tree.getParent(node) != null) {
			this.backpropagate(result, this.tree.getParent(node));
		}
	}
	
	private A bestAction(final Node<S, A> root) {
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
		for (final A a : this.game.getActions(node.getState())) {
			final S result = this.game.getResult(node.getState(), a);
			if (!visitedChildren.contains(result)) unvisitedChildren.add(result);
		}
		final Random rand = new Random();
		return this.tree.addChild(node, unvisitedChildren.get(rand.nextInt(unvisitedChildren.size())));
	}
	
	@Override
	public Metrics getMetrics() {
		return null;
	}
}
