package type;

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
    public static final Type UNIT = new Type("_unit");
    
    /**
     * Primitive void.
     * 
     * Represents the type of statements that never
     * complete normally.
     */
    public static final Type VOID = new Type("_void");
    
    /**
     * Primitive poly.
     * 
     * Represents the type of polymorphic length-0 arrays.
     */
    public static final Type POLY = new Type(new Type("_poly")); // For the empty array {}

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
    public ArrayList<Type> children;

    /**
     * Creates a new primitive type.
     */
    protected Type(String id) {
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
     * Creates a new tuple type of its argument.
     * 
     * @param children Types to create a tuple of, in order
     */
    public Type(ArrayList<Type> children) {
    	this.kind = Kind.TUPLE;
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
     */
    public boolean equals(Object o) {
        if (!(o instanceof Type)) { return false; }

        Type t = (Type) o;

        if (kind == Kind.CLASS && t.kind == Kind.CLASS) {
            return t.id.equals(id);
        } else if (kind == Kind.ARRAY && t.kind == Kind.ARRAY) {
            return t.children.get(0).equals(children.get(0)) || t == Type.POLY || this == Type.POLY;
        } else if (kind == Kind.TUPLE && t.kind == Kind.TUPLE) {
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
}