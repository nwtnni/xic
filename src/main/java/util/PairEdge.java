package util;

/** 
 * A wrapper class for edges that contain a pair of thehead and tail node
 * and a value.
 */
public class PairEdge<V, E> {
    public V head;
    public V tail;
    public E value;

    public PairEdge(V a, V b, E v) {
        this.head = a;
        this.tail = b;
        this.value = v;
    }

    @Override
    public int hashCode() {
        return 17 * head.hashCode() ^ 19 * tail.hashCode();
    }

    // TODO: this is sort of a hack to get remove working with JGraphT
    /** PairEdge equality is only defined by head and tail node equality. */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PairEdge<?, ?>) {
            PairEdge<?, ?> e = (PairEdge<?, ?>) o;
            return head.equals(e.head) && tail.equals(e.tail);
        } 
        return false;
    }

}
