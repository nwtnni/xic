package type;

import java.util.ArrayList;

import ast.*;
import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

/**
 * Represents a function type as defined the Xi Type Specification.
 * 
 * Functions can have any number of argument and return types. This
 * class visits a {@link ast.Fn} node and its {@link ast.XiType} children
 * in order to do its conversions.
 * 
 * @see Type
 * @see ast.Visitor
 */
public class FnType extends Visitor<Type> {

	/**
	 * Convenience factory method to create a FnType from a Fn.
	 * 
	 * @param f Fn node to convert
	 * @return FnType corresponding to Fn f
	 */
	public static FnType from(Fn f) {
		FnType type = new FnType();
		type.visit(f);
		return type;
	}

	/**
	 * Stores the Fn's location for error display.
	 */
	public Location location;
	
	/**
	 * Stores the Fn's arguments. Can be a {@link Type.Kind#TUPLE} for
	 * functions with zero or more than one argument types, or a {@link Type.Kind#CLASS}
	 * for functions with one return type.
	 */
	public Type args;
	
	/**
	 * Stores the Fn's return types. Can be a {@link Type.Kind#TUPLE} for
	 * functions with zero or more than one return types, or a {@link Type.Kind#CLASS}
	 * for functions with one return type.
	 */
	public Type returns;

	/**
	 * Visits a Fn's children.
	 */
    public Type visit(Fn f) {
    	try {
    		location = f.location;
    		args = f.args.accept(this);
    		returns = f.returns.accept(this);
    	} catch (XicException xic) {
    		// Unreachable
    		assert false;
    	}
    	return null;
    }
    
    /**
     * Visits a Multiple's children and collects them into a tuple.
     */
    public Type visit(Multiple m) throws XicException {
    	if (m.values.size() == 0) { 
			return Type.UNIT; 
		}

    	ArrayList<Type> types = new ArrayList<>();
    	for (Node value : m.values) {
    		types.add(value.accept(this));
    	}
		switch (m.kind) {
			case FN_RETURNS:
				return new Type(types, false);
			case FN_ARGS:
				return new Type(types, true);
		}
		// Unreachable
		assert false;
		return null;
    }

    /**
     * Visits a Declare node and returns its type.
     */
    public Type visit(Declare d) throws XicException {
    	return d.xiType.accept(this);
    }

    /**
     * Visits a XiType node and converts it to a Type.
     */
    public Type visit(XiType xt) {
    	return new Type(xt);
    }

    /**
     * Two functions are equal if their argument and return types
     * are equal.
     */
    @Override
    public boolean equals(Object o) {
    	if (!(o instanceof FnType)) { return false; }
    	FnType type = (FnType) o;
    	return args.equals(type.args) && returns.equals(type.returns);
    }

    /**
     * Implemented to maintain equivalence with equals.
     */
    @Override
    public int hashCode() {
    	return args.hashCode() * returns.hashCode();
    }
}
