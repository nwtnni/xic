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

	private FnType() {
    	args = new Type(new ArrayList<Type>());
    	returns = new Type(new ArrayList<Type>());
	}

    public Type visit(Fn f) {
    	location = f.location;
    	try {
	    	for (Node declaration : f.args) {
	    		args.children.add(declaration.accept(this));
	    	}
	    	for (Node type : f.returns) {
	    		returns.children.add(type.accept(this));
	    	}
    	} catch (XicException xic) { assert false; }
    	return null;
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

    	if (type.args.children.size() != args.children.size()) { return false; }
    	if (type.returns.children.size() != returns.children.size()) { return false; }

    	for (int i = 0; i < args.children.size(); i++) {
    		if (!type.args.children.get(i).equals(args.children.get(i))) {
    			return false;
    		}
    	}

    	for (int i = 0; i < returns.children.size(); i++) {
    		if (!type.returns.children.get(i).equals(returns.children.get(i))) {
    			return false;
    		}
    	}

    	return true;
    }

    @Override
    public int hashCode() {
    	int hash = 1;
    	for (int i = 0; i < args.children.size(); i++) {
    		hash *= args.children.get(i).hashCode();
    	}

    	for (int i = 0; i < returns.children.size(); i++) {
    		hash *= returns.children.get(i).hashCode();
    	}
    	return hash;
    }
}
