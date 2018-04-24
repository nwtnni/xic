package util;

/** 
 * A wrapper class for edges that contain a pair of thehead and tail node
 * and a value.
 */
public class PairEdge<V, E> {
    Pair<V, V> nodes;
    E value;

    public PairEdge(V a, V b, E v) {
        this.nodes = new Pair<>(a, b);
        this.value = v;
    }

}