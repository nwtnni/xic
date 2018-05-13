package type;

import util.Context;

import org.pcollections.PMap;

/**
 * Symbol table mapping {@link XiFn#id Fn} id to {@link FnType}. Supports merging two
 * FnContexts together, in order to check interfaces against definitions.
 * 
 * @see Context
 */
public class FnContext extends Context<String,FnType> {
    
    /**
     * Adds all bindings in FnContext other to this context
     * 
     * @param other FnContext to merge
     * @throws TypeException if a binding exists in this context and other,
     *             but the bound types differ
     */
    public void merge(FnContext other) throws TypeException {
        for (PMap<String,FnType> map : other.context) {
            for (String id : map.keySet()) {
                FnType existing = lookup(id);
                FnType type = other.lookup(id);
                if (existing == null) {
                    add(id, type);
                } else if (!existing.equals(type)) {
                    throw new TypeException(TypeException.Kind.DECLARATION_CONFLICT, existing.location);
                }
            }
        }
    }
}