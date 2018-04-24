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
    public boolean equals(Object o) {
        if (o instanceof PairEdge<?, ?>) {
            PairEdge<?, ?> e = (PairEdge<?, ?>) o;
            return head.equals(e.head) && tail.equals(e.tail) && value.equals(e.value);
        } 
        return false;
    }

}
