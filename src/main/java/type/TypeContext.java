package type;

/**
 * Symbol table mapping {@link Type} to supertype. Not strictly
 * necessary until we implement user-defined types.
 *
 * @see Context
 */
public class TypeContext extends Context<Type, Type> {

	/**
	 * Default constructor initializes context with primitive types.
	 * 
	 * All primitive types are subtypes of {@link Type.Kind#UNIT}.
	 */
    public TypeContext() {
        super();
        try {
            add(Type.BOOL, Type.UNIT);
            add(Type.INT, Type.UNIT);
            add(Type.VOID, Type.UNIT);
            add(Type.UNIT, null);
        } catch (TypeException e) {
        	// Unreachable
        	assert false;
        } 
    }

    /**
     * Validates a subtyping relationship by checking for cyclic dependencies.
     * 
     * @param child Subtype to add
     * @param parent Supertype to add
     * @throws TypeException if this relationship will cause a cyclic dependency
     */
    public void validate(Type child, Type parent) throws TypeException {
        Type ancestor = parent;
        while (ancestor != null) {
            if (ancestor.equals(child)) {
                throw new TypeException(TypeException.Kind.CYCLIC_TYPE_DEPENDENCY);
            }
            ancestor = lookup(ancestor);
        }
    }

    /**
     * Checks if child is a subtype of parent.
     * 
     * @param child Subtype to check
     * @param parent Supertype to check
     * @return True if child is a subtype of parent, else false
     */
    public boolean isSubType(Type child, Type parent) {
        if (child.equals(Type.UNIT)) {
            return false;
        }
        switch (child.kind) {
            case CLASS:
            	
            	// Upward tree traversal must find parent
                Type ancestor = child;
                while (ancestor != null) {
                    if (ancestor.equals(parent)) {
                        return true;
                    }
                    ancestor = lookup(ancestor);
                }
                return false;
                
            case TUPLE:
            	
            	// Tuples can only be subclassed by tuples
                if (parent.kind != Type.Kind.TUPLE) { return false; }
                
                // Tuples must have the same number of children
                int csize = child.children.size();
                int psize =  parent.children.size();
                if (csize != psize) { return false; }
                
                // Each child must be a subtype of the other
                for (int i = 0; i < csize; i++) {
                    if (!isSubType(child.children.get(i), parent.children.get(i))) {
                        return false;
                    }
                }
                return true;
            case ARRAY:
            	
            	// No subclassing for arrays, unless wildcard
            	return child.equals(parent) || parent.equals(Type.UNIT);
        }
        
        // Unreachable
        assert false;
        return false;
    }
}
