package graph;

import util.Pair;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Graph<V extends Node, E> {
	
	protected Map<V, List<Pair<V, E>>> graph;
	protected Set<V> nodes;

	public Graph() {
		graph = new HashMap<>();
		nodes = new HashSet<>();
	}
	
	public boolean contains(V a, V b) {
		if (!graph.containsKey(a)) return false;
		
		return graph.get(a)
			.stream()
			.anyMatch(p -> p.first.equals(b));
	}
	
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
		
		if (graph.get(a).isEmpty()) {
			graph.remove(a);
			nodes.remove(a);
		}
	}
	
	public void insert(V a, V b, E w) {
		remove(a, b);
		graph.putIfAbsent(a, new ArrayList<>());
		graph.get(a).add(new Pair<>(b, w));
		nodes.add(a);
		nodes.add(b);
	}
	
	public int size() {
		return graph.size();
	}
	
	/**
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
	
	public List<Pair<V, E>> neighbors(V a) {
		if (graph.containsKey(a)) {
			return graph.get(a);
		} else {
			return new ArrayList<>();
		}
	}
	
	public List<V> topological() {
		Set<V> unvisited = new HashSet<>(nodes);
		Stack<V> stack = new Stack<>();
		List<V> sorted = new ArrayList<>();
		
		while (!unvisited.isEmpty()) {
			
			V start = unvisited
					.stream()
					.findAny()
					.get();
		
			stack.push(start);
			
			while (!stack.isEmpty()) {
				V node = stack.pop();
				sorted.add(node);
				unvisited.remove(node);
				
				for (Pair<V, E> edge : neighbors(node)) {
					if (unvisited.contains(edge.first)) {
						stack.push(edge.first);
					}
				}
			}
		}
		return sorted;
	}
}
