package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.tree;

import java.util.*;
import java.util.function.BiFunction;

/**
 *	Basic implementation of Game Tree for the Monte Carlo Tree Search
 *
 * 	Wi stands for the number of wins for the node considered after the i-th move.
 * 	Ni stands for the number of simulations for the node considered after the i-th move.
 *
 * @author Suyash Jain
 */

public class GameTree<S, A> {
	final private Map<Node<S, A>, List<Node<S, A>>> gameTree;
	final private Map<S, Double> Wi, Ni;
	final private NodeFactory<S, A> nodeFactory;
	private Node<S, A> root;

	public GameTree() {
		this.gameTree = new HashMap<>();
		this.nodeFactory = new NodeFactory<>();
		this.Wi = new HashMap<>();
		this.Ni = new HashMap<>();
	}

	public Map<S, Double> getWi() {
		return this.Wi;
	}

	public Map<S, Double> getNi() {
		return this.Ni;
	}

	public void addRoot(final S root) {
		final Node<S, A> rootNode = this.nodeFactory.createNode(root);
		this.root = rootNode;
		this.gameTree.put(rootNode, new ArrayList<>());
		this.Wi.put(root, 0.0);
		this.Ni.put(root, 0.0);
	}
	
	public Node<S, A> getRoot() {
		return this.root;
	}
	
	public List<S> getVisitedChildren(final Node<S, A> parent) {
		final List<S> visitedChildren = new ArrayList<>();
		if (this.gameTree.containsKey(parent)) {
			for (final Node<S, A> child : this.gameTree.get(parent)) {
				visitedChildren.add(child.getState());
			}
		}
		return visitedChildren;
	}
	
	public Node<S, A> addChild(final Node<S, A> parent, S child, A action) {
		final Node<S, A> newChild = this.nodeFactory.createNode(child, parent, action, 1);
		final List<Node<S, A>> children = successors(parent);
		children.add(newChild);
		this.gameTree.put(parent, children);
		this.Wi.put(child, 0.0);
		this.Ni.put(child, 0.0);
		return newChild;
	}
	
	public List<Node<S, A>> successors(final Node<S, A> node) {
		if (this.gameTree.containsKey(node)) {
			return this.gameTree.get(node);
		}
		else {
			return new ArrayList<>();
		}
	}
	
	public void updateStats(final boolean result, final Node<S, A> node) {
		this.Ni.put(node.getState(), this.Ni.get(node.getState()) + 1);
		if (result) {
			this.Wi.put(node.getState(), this.Wi.get(node.getState()) + 1);
		}
	}

	// TODO UCB1-tuned
	public Node<S, A> getChildWithMaxUCT(final Node<S, A> node) {
		List<Node<S, A>> best_children = new ArrayList<>();
		double max_uct = Double.NEGATIVE_INFINITY;
		for (final Node<S, A> child : successors(node)) {
			double uct = ((this.Wi.get(child.getState())) / (this.Ni.get(child.getState()))) + Math.sqrt((2 / this.Ni.get(child.getState())) * (Math.log(this.Ni.get(node.getState()))));
			if (uct > max_uct) {
				max_uct = uct;
				best_children = new ArrayList<>();
				best_children.add(child);
			} else if (uct == max_uct) {
				best_children.add(child);
			}
		}

		final Random rand = new Random();
		return best_children.get(rand.nextInt(best_children.size()));
	}
	
	public Node<S, A> getChildWithMaxPlayouts(final Node<S, A> node) {
		List<Node<S, A>> best_children = new ArrayList<>();
		double max_playouts = Double.NEGATIVE_INFINITY;
		for (final Node<S, A> child : successors(node)) {
			double playouts = (this.Ni.get(child.getState()));
			if (playouts > max_playouts) {
				max_playouts = playouts;
				best_children = new ArrayList<>();
				best_children.add(child);
			} else if (playouts == max_playouts) {
				best_children.add(child);
			}
		}
		final Random rand = new Random();
		return best_children.get(rand.nextInt(best_children.size()));
	}
}
