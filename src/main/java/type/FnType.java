package type;

import java.util.ArrayList;

import ast.*;
import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

public class FnType extends Visitor<Type> {

	public static FnType from(Fn f) {
		FnType type = new FnType();
		type.visit(f);
		return type;
	}

	public Location location;
	public Type args;
	public Type returns;

    public Type visit(Fn f) {
    	try {
    		location = f.location;
    		args = f.args.accept(this);
    		returns = f.returns.accept(this);
    	} catch (XicException xic) {
    		//TODO assert unreachable?
    		assert false;
    	}
    	return null;
    }
    
    public Type visit(Multiple m) throws XicException {
    	if (m.values.size() == 0) { return Type.UNIT; }
    	ArrayList<Type> types = new ArrayList<>();
    	for (Node value : m.values) {
    		types.add(value.accept(this));
    	}
    	return new Type(types);
    }

    public Type visit(Declare d) throws XicException {
    	return d.type.accept(this);
    }

    public Type visit(XiType xt) {
    	return new Type(xt);
    }

    @Override
    public boolean equals(Object o) {
    	if (!(o instanceof FnType)) { return false; }
    	FnType type = (FnType) o;
    	return args.equals(type.args) && returns.equals(type.returns);
    }

    @Override
    public int hashCode() {
    	return args.hashCode() * returns.hashCode();
    }
}
