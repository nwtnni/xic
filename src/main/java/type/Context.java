package type;

import org.pcollections.*;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

import type.TypeException.Kind;
import xic.XicInternalException;

/**
 * Persistent implementation of a generic symbol table. Reinforces uniqueness of symbols.
 * 
 * This class is backed by the <a href="https://pcollections.org/">PCollections</a>
 * library, which offers several implementations of persistent data structures.
 * Since cloning is cheap, we can afford to cache the symbol table while traversing
 * the AST.
 * 
 * @param <S> Symbol representation
 * @param <T> Type representation
 *
 * @see FnContext
 * @see TypeContext
 * @see VarContext
 */
public abstract class Context<S,T> {

	/**
	 * The backing persistent data structure.
	 * 
	 * We use a PStack to represent entering and leaving different scopes,
	 * and PMaps to record key-value pairs.
	 */
    protected PStack<PMap<S,T>> context;

    /**
     * Default constructor initializes a single empty map.
     */
    public Context() {
        this.context = ConsPStack.singleton(HashTreePMap.empty());
    }
    
    /**
     * Internal constructor to clone a Context.
     * 
     * @param c Context to clone
     */
    protected Context(Context<S,T> c) {
    	this.context = c.context;
    }

    /**
     * Traverses the context and searches for an existing binding to Key k.
     * 
     * @param s Symbol to search for
     * @return Type t if it exists in this context, else null
     */
    public T lookup(S s) {
        for (PMap<S,T> map : context) {
            T t = map.get(s);
            if (t != null){
                return t;
            }
        }
        return null;
    }

    /**
     * Adds a unique binding to this context.
     * 
     * @param s Symbol to add
     * @param t Type to bind s to
     * @throws TypeException if Symbol s is already bound in this context
     */
    public void add(S s, T t) throws TypeException {
    	//TODO throwing here means we lose location information?
        if (contains(s)) { throw new TypeException(Kind.DECLARATION_CONFLICT); }
        PMap<S, T> map = context.get(0);
        context = context.minus(0);
        map = map.plus(s, t);
        context = context.plus(map);
    }

    /**
     * Pushes a new scope onto the stack.
     */
    public void push() {
    	context = context.plus(HashTreePMap.empty());
    }

    /**
     * Pops the last scope off the stack.
     */
    public void pop() {
        if (context.size() > 1) {
            context = context.minus(0);
        }
        throw XicInternalException.internal("Cannot remove global context.");
    }

    /**
     * Checks if Symbol s is bound in this context.
     * 
     * @param s Symbol to look up
     * @return true if s is bound, else false
     */
    public boolean contains(S s) {
        return lookup(s) != null;
    }
    
    /**
     * Returns a Map of all the bindings in the context
     */
    public Map<S,T> getMap() {
        Map<S,T> aggregateMap = new LinkedHashMap<>();
        for (PMap<S,T> map : context) {
            Iterator<Map.Entry<S,T>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<S,T> e = (Map.Entry<S,T>) it.next();
                aggregateMap.put(e.getKey(), e.getValue());
            }
        }
        return aggregateMap;
    }
}
