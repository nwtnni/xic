package type;

import java.util.List;

import java.util.ArrayList;

import ast.XiType;

/**
 * Represents a type as defined in the Xi Type Specification.
 * 
 * Does not differentiate between statement types and expression types.
 * Types are represented as a tree of Type nodes. Requires that
 * all leaves of the tree are primitive types.
 * 
 * @see FnType
 */
public class Type {

	/**
	 * Primitive int.
	 */
    public static final Type INT = new Type("int");
    
    /**
     * Primitive bool.
     */
    public static final Type BOOL = new Type("bool");
    
    /**
     * Primitive unit.
     * 
     * Represents the type of both wildcards and
     * statements that might complete normally.
     */
    public static final Type UNIT = new Type();
    
    /**
     * Primitive void.
     * 
     * Represents the type of statements that never
     * complete normally.
     */
    public static final Type VOID = new Type("_void");
    
    private static final String POLY_ID = "_poly";

    /**
     * Primitive poly.
     * 
     * Represents the type of polymorphic length-0 arrays.
     */
    public static final Type POLY = new Type(new Type(POLY_ID));

    /**
     * Factory method for making a type from a list of types.
     * 
     * @param children list of children types
     * @param isList is true if the type is a list type
     */
    private static Type fromList(List<Type> children, boolean isList) {
        switch (children.size()) {
            case 0:
                return new Type();
            case 1:
                return children.get(0);
            default:
                return new Type(children, isList);
        }
    }

    /**
     * Factory method for making a list type from a list of types.
     * Returns a class type if the length of the list is one.
     * 
     * @param children list of children types
     */
    public static Type listFromList(List<Type> children) {
        return fromList(children, true);
    }

    /**
     * Factory method for making a tuple type from a list of types.
     * Returns a class type if the length of the list is one.
     * 
     * @param children list of children types
     */
    public static Type tupleFromList(List<Type> children) {
        return fromList(children, false);
    }

    /**
     * Denotes the possible categories of Types.
     */
    public enum Kind {
    	
    	/**
    	 * Represents an array type constructor
    	 */
        ARRAY, 
        
        /**
         * Represents a single type
         */
        CLASS,

        /**
         * Represents a list type constructor
         */
        LIST,
        
        /**
         * Represents a tuple type constructor
         */
        TUPLE
    }
    
    
    /**
     * Records the Kind of this Type
     */
    public Kind kind;
    
    /**
     * The name of this Type, if it isn't a Type constructor
     */
    public String id;
    
    /**
     * The children of this Type, if it is a Type constructor
     */
    public List<Type> children;

    /**
     * Creates a unit type.
     */
    private Type() {
        this.kind = Kind.TUPLE;
        this.id = "_unit";
        this.children = new ArrayList<>();
    }

    /**
     * Creates a new class type. Primitives also fall under this category.
     */
    public Type(String id) {
        this.kind = Kind.CLASS;
        this.id = id;
        this.children = null;
    }

    /**
     * Creates a new array type of its argument.
     * 
     * @param child Type to create an array of
     */
    public Type(Type child) {
        this.kind = Kind.ARRAY;
        this.id = null;
        this.children = new ArrayList<>();
        this.children.add(child);
    }
    
    /**
     * Creates a new list or tuple type of its argument requires that
     * the number of elements in children is greater than 1.
     * 
     * @param children Types to create a list of, in order
     * @param isList is true if the type is a list type
     */
    private Type(List<Type> children, boolean isList) {
        assert children.size() > 1;
        this.kind = isList ? Kind.LIST : Kind.TUPLE;
        this.id = null;
        this.children = children;
    }

    /**
     * Convenience constructor to convert a {@link ast.XiType} node
     * into a corresponding Type node.
     * 
     * @param xt XiType node to convert
     */
    public Type(XiType xt) {
        if (xt.isClass()) {
            this.kind = Kind.CLASS;
            this.id = xt.id;
            this.children = null;
        } else {
            this.kind = Kind.ARRAY;
            this.id = null;
            this.children = new ArrayList<>(); 
            children.add(new Type((XiType) xt.child));
        }
    }
    
    /**
     * Checks for equality of the Type tree. Special case is the
     * {@link Type#POLY} class, which is equal to any array type.
     * Automatically does type coercion for polymorphic arrays.
     */
    public boolean equals(Object o) {
        if (!(o instanceof Type)) { return false; }

        Type t = (Type) o;

        if (kind == Kind.CLASS && t.kind == Kind.CLASS) {
            if (id.equals(POLY_ID)) {
                id = t.id;
            }
            if (t.id.equals(POLY_ID)) {
                t.id = id;
            }
            return t.id.equals(id);
        } else if (kind == Kind.ARRAY && t.kind == Kind.ARRAY) {
            return t.children.get(0).equals(children.get(0)) || this == POLY || t == POLY;
        } else if (kind == Kind.TUPLE && t.kind == Kind.TUPLE ||
                   kind == Kind.LIST && t.kind == Kind.LIST) {
        	if (children.size() != t.children.size()) { return false; }
        	for (int i = 0; i < children.size(); i++) {
        		if (!children.get(i).equals(t.children.get(i))) {
        			return false;
        		}
        	}
            return true;
        } else {
        	return false;
        }
    }

    /**
     * Implemented to maintain equivalence with {@link equals}.
     */
    public int hashCode() {
        if (kind == Kind.CLASS) {
            return id.hashCode();
        } else if (kind == Kind.ARRAY) {
        	return 100;
        } else {
        	int hash = 1;
        	for (Type child : children) {
        		hash += 10 * child.hashCode();
        	}
        	return hash;
        }
    }

    /**
     * Returns true if an arbitrary dimension array is polymorphic.
     */
    public boolean isPoly() {
        if (kind == Kind.ARRAY) {
            if (this == POLY) {
                return true;
            }
            return this.children.get(0).isPoly();
        } else {
            return false;
        }
    }

    /**
     * Returns true if type is primitive.
     */
    public boolean isPrimitive() {
        return this.equals(INT) || this.equals(BOOL);

    }

    /**
     * Returns true if type is an array type.
     */
    public boolean isArray() {
        return kind.equals(Kind.ARRAY);
    }

    @Override
    public String toString() {
        switch (kind) {
            case CLASS:
                if (this.equals(INT)) {
                    return "i";
                } else if (this.equals(BOOL)) {
                    return "b";
                } else if (this.equals(UNIT)) {
                    return "";
                } else {
                    // TODO for future extension
                    assert false;
                    return null;
                }
            case ARRAY:
                return "a" + children.get(0).toString();
            case LIST:
                String args = "";
                for (Type t : children) {
                    args += t.toString();
                }
                return args;
            case TUPLE:
                if (children.size() == 0) {
                    return "";
                }
                String encoding = "";
                for (Type t : children) {
                    encoding += t.toString();
                }
                return "t" + children.size() + encoding;
        }
        // Unreachable
        assert false;
        return null;
    }
}
