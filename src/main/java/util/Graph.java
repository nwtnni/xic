package util;

import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/** Deprecated in favor of JGraphT */
@Deprecated
public class Graph<V, E> {
	
	/** 
	 * The adjaceny of all edges in the graph. 
	 * For each key a there must be a corresponding element a in nodes.
	 * For each edge a -> b there must be a corresponding edge in transposed
	 */
	protected Map<V, List<Pair<V, E>>> graph;

	/** 
	 * The adjacency of all transposed edges in the graph. 
	 * Maintains same invariants as graph except edges are transposed.
	*/
	protected Map<V, List<Pair<V, E>>> transposed;

	/** 
	 * Set of all nodes in the graph.
	 * If there is a node a in nodes then there must be a 
	 * corresponding entry in graph.
	 */
	protected Set<V> nodes;

	public Graph() {
		graph = new HashMap<>();
		nodes = new HashSet<>();
	}
	
	/** Returns true if graph contains an edge a -> b. */
	public boolean contains(V a, V b) {
		if (!graph.containsKey(a)) return false;
		
		return graph.get(a)
			.stream()
			.anyMatch(p -> p.first.equals(b));
	}
	
	/** Removes an edge a -> b from the graph. */
	public void remove(V a, V b) {
		if (!contains(a, b)) {
			return;
		}
		
		List<Pair<V, E>> adj = graph.get(a);
		
		for (int i = 0; i < adj.size(); i++) {
			if (adj.get(i).first.equals(b)) {
				adj.remove(i);
				break;
			}
		}

		// TODO: update transposed
	}

	/** 
	 * Removes a node a from the graph as well as deleting all edges in 
	 * and out of that node. Returns a pair of the list of in edges and 
	 * the list of out edges from a.
	 */
	public Pair<List<Pair<V, E>>, List<Pair<V, E>>> remove(V a) {
		// TODO: implement this
		return null;
	}
	
	/** 
	 * Adds a node a into the graph. Updates the adjacency lists with 
	 * an empty list if one does not already exist.
	*/
	public void insert(V a) {
		nodes.add(a);
		graph.putIfAbsent(a, new ArrayList<>());
	}
	
	/** 
	 * Adds an edge a -> b with value w. Inserts a and b and their
	 * adjacency lists  if they do not already exist.
	*/
	public void put(V a, V b, E w) {
		remove(a, b);
		nodes.add(a);
		graph.putIfAbsent(a, new ArrayList<>());
		nodes.add(b);
		graph.putIfAbsent(b, new ArrayList<>());
		graph.get(a).add(new Pair<>(b, w));

		// TODO: update transposed
	}
	
	/** Returns the number of nodes in the graph. */
	public int size() {
		return graph.size();
	}
	
	/**
	 * Returns the value stored on edge a -> b.
	 * Requires: graph contains edge from a -> b.
	 */
	public E get(V a, V b) {
		return graph.get(a)
			.stream()
			.filter(p -> p.first.equals(b))
			.findFirst()
			.map(p -> p.second)
			.get();
	}
	
	/**
	 * Returns the list of edges to successor nodes of a.
	 */
	public List<Pair<V, E>> successors(V a) {
		if (graph.containsKey(a)) {
			return graph.get(a);
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * Returns the list of edges to predecessor nodes of a.
	 */
	public List<Pair<V, E>> predecessors(V a) {
		// TODO: do this
		return null;
	}

	/** Returns a new graph that is a transposed copy of this graph. */
	public Graph<V,E> transpose() {
		Graph<V, E> transposed = new Graph();

		for (V node : nodes) {
			transposed.insert(node);
		}

		for (Map.Entry<V, List<Pair<V, E>>> entry : graph.entrySet()) {
			for (Pair<V, E> edge : entry.getValue()) {
				transposed.put(edge.first, entry.getKey(), edge.second);
			}
		}

		return transposed;
	}
}
