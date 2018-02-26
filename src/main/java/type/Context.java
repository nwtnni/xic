package type;

import org.pcollections.*;

public class Context<K, V> {

    private PStack<PMap<K, V>> context;

    public Context() {
        this.context = ConsPStack.singleton(HashTreePMap.empty());
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
        PMap<K, V> map = context.get(0);
        context = context.minus(0);
        map = map.plus(k, t);
        context = context.plus(map);
    }

    public void push() {
    	context = context.plus(HashTreePMap.empty());
    }

    public void pop() {
    	context = context.minus(0);
    }

    public boolean inContext(K id) {
		if (context.get(0).containsKey(id)){
			return true;
		}
		return false;
	}
}
