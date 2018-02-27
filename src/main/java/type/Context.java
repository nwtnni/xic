package type;

import org.pcollections.*;

import type.TypeException.Kind;

public class Context<K, V> {

    private PStack<PMap<K, V>> context;

    public Context() {
        this.context = ConsPStack.singleton(HashTreePMap.empty());
    }

    public V lookup(K k) {
        for (PMap<K, V> map : context) {
            V v = map.get(k);
            if (v != null){
                return v;
            }
        }
        return null;
    }

    public void add(K k, V t) throws TypeException {
        if (lookup(k) != null) {
            throw new TypeException(Kind.DECLARATION_CONFLICT);
        }
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
        return lookup(id) != null;
	}
}
