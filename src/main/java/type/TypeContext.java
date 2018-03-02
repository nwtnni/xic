package type;

/**
 * Symbol table mapping {@link Type} to supertype.
 * 
 * Not strictly necessary for now, since we don't have user-defined types,
 * but implemented for future-proofing purposes. TypeContext maintains
 * a tree mapping child types to parent types.
 */
public class TypeContext extends Context<Type, Type> {

	/**
	 * Default constructor initializes the context with primitive types.
	 */
    public TypeContext() {
        super();
        try {
            add(Type.BOOL, Type.UNIT);
            add(Type.INT, Type.UNIT);
            add(Type.POLY, Type.UNIT);
            add(Type.VOID, Type.UNIT);
            add(Type.UNIT, null);
        } catch (TypeException e) {
            // impossible when constructing new clean TypeContext
        } 
    }

    /**
     * Checks for cyclic type dependencies.
     * @param child Child type to check
     * @param parent Parent type to subclass 
     * @throws TypeException if a cyclic type dependency occurs as a result of this subclassing relationship.
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
     * Checks for a subtyping relationship.
     * @param child Child type to check
     * @param parent Parent type to check
     * @return
     */
    public boolean isSubType(Type child, Type parent) {
        if (child.equals(Type.UNIT)) {
            return false;
        }
        switch (child.kind) {
            case CLASS:
                Type ancestor = child;
                while (ancestor != null) {
                    if (ancestor.equals(parent)) {
                        return true;
                    }
                    ancestor = lookup(ancestor);
                }
                return false;
            case TUPLE:
                if (!child.kind.equals(parent.kind)) {
                    return false;
                }
                int cs = child.children.size();
                int ps =  parent.children.size();
                if (cs != ps) {
                    return false;
                }
                for (int i = 0; i < cs; i++) {
                    if (!isSubType(child.children.get(i), parent.children.get(i))) {
                        return false;
                    }
                }
                return true;
            case ARRAY:
            	return child.equals(parent) || parent.equals(Type.UNIT);
        }
        // Unreachable
        assert false;
        return false;
    }
}
