package util;

import org.jgrapht.*;

import util.PairEdge;

/** Superclass for all pair edges. */
public interface PairEdgeFactory<V, E> extends EdgeFactory<V, PairEdge<V, E>> {
}
