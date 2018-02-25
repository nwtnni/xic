package type;

import org.pcollections.*;

public class Context<K, V> {

    private PStack<PMap<K, V>> context;

    public Context() {
        this.context = ConsPStack.singleton(HashTreePMap.empty());
    }

    public Context(Context<K, V> c) {
        this.context = c.context.plus(HashTreePMap.empty());
    }

    public V lookup(K k) throws Exception {
        for (PMap<K, V> map : context) {
            V v = map.get(k);
            if (v != null){
                return v;
            }
        }
        throw new Exception("Unbound value: " + k);
    }

    public void add(K k, V t) {
        PMap<K, V> map = this.context.get(0);
        this.context = this.context.minus(0);
        map = map.plus(k, t);
        this.context = this.context.plus(map);
    }
}
