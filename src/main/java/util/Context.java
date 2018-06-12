package util;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Iterator;

import xic.XicInternalException;

/**
 * Generic symbol table. Reinforces uniqueness of symbols.
 * 
 * @param <S> Symbol representation
 * @param <T> Type representation
 *
 * @see FnContext
 * @see TypeContext
 * @see VarContext
 */
public class Context<S,T> {

    /**
     * We use a Stack to represent entering and leaving different scopes,
     * and Maps to record key-value pairs.
     */
    protected Stack<Map<S,T>> context;

    /**
     * Default constructor initializes a single empty map.
     */
    public Context() {
        this.context = new Stack<>();
        this.context.push(new LinkedHashMap<>());
    }

    /**
     * Traverses the context and searches for an existing binding to Key k.
     * 
     * @param s Symbol to search for
     * @return Type t if it exists in this context, else null
     */
    public T lookup(S s) {
        for (Map<S,T> map : context) {
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
     * @throws XicInternalException if Symbol s is already bound in this context
     */
    public void add(S s, T t) {
        if (contains(s)) { throw XicInternalException.runtime("Shadowing key in context"); }
        context.peek().put(s, t);
    }

    /**
     * Pushes a new scope onto the stack.
     */
    public void push() {
        context.push(new LinkedHashMap<>());
    }

    /**
     * Pops the last scope off the stack.
     */
    public void pop() {
        if (context.size() > 1) {
            context.pop();
            return;
        }
        throw XicInternalException.runtime("Cannot remove global context.");
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
     * Returns a Map of all the bindings in this context.
     */
    public Map<S,T> getMap() {
        Map<S,T> aggregateMap = new LinkedHashMap<>();
        for (Map<S,T> map : context) {
            Iterator<Map.Entry<S,T>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<S,T> e = it.next();
                aggregateMap.put(e.getKey(), e.getValue());
            }
        }
        return aggregateMap;
    }
}
