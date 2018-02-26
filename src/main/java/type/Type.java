package type;

import java.util.ArrayList;

import ast.XiType;

public class Type {

    public static final Type INT = new Type("int");
    public static final Type BOOL = new Type("bool");
    public static final Type UNIT = new Type("_unit");
    public static final Type VOID = new Type("_void");
    public static final Type POLY = new Type("_poly");
    public static final Type EMPTY = new Type("_empty");

    public static final Type[] TYPES = {INT, BOOL, UNIT, VOID, POLY};

    public enum Kind {
        ARRAY, CLASS, TUPLE
    }

    public Kind kind;
    public String id;
    public ArrayList<Type> children;

    protected Type(String id) {
        this.kind = Kind.CLASS;
        this.id = id;
        this.children = null;
    }

    public Type(Type child) {
        this.kind = Kind.ARRAY;
        this.id = null;
        this.children = new ArrayList<>();
        this.children.add(child);
    }
    
    public Type(ArrayList<Type> children) {
    	this.kind = Kind.TUPLE;
    	this.id = null;
    	this.children = children;
    }

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
    
    protected Type() {}
    
    public String getVariable() {
    	return null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) { return false; }

        Type t = (Type) o;

        if (kind == Kind.CLASS && t.kind == Kind.CLASS) {
            return t.id.equals(id);
        } else if (kind == Kind.ARRAY && t.kind == Kind.ARRAY) {
            return t.children.get(0).equals(children.get(0));
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

    @Override
    public int hashCode() {
        if (kind == Kind.CLASS) {
            return id.hashCode();
        } else {
        	int hash = 1;
        	for (Type child : children) {
        		hash += 10 * child.hashCode();
        	}
        	return hash;
        }
    }
}
