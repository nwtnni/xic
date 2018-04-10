package graph;

import util.Pair;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Graph<V extends Node, E> {
	
	protected Map<V, List<Pair<V, E>>> graph;

	public Graph() {
		graph = new HashMap<>();
	}
	
	public boolean contains(V a, V b) {
		if (!graph.containsKey(a)) return false;
		
		return graph.get(a)
			.stream()
			.anyMatch(p -> p.first.equals(b));
	}
	
	public void remove(V a, V b) {
		if (!contains(a, b)) return;
		List<Pair<V, E>> adj = graph.get(a);
		for (int i = 0; i < adj.size(); i++) {
			if (adj.get(i).first.equals(b)) {
				adj.remove(i);
				return;
			}
		}
	}
	
	public void insert(V a, V b, E w) {
		graph.putIfAbsent(a, new ArrayList<>());
		remove(a, b);
		graph.get(a).add(new Pair<>(b, w));
	}
}
