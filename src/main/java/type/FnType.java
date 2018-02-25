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
	public ArrayList<Type> args;
	public ArrayList<Type> returns;
	
	private FnType() {
    	args = new ArrayList<>();
    	returns = new ArrayList<>();
	}
		
    public Type visit(Fn f) {
    	location = f.location;
    	try {
	    	for (Node declaration : f.args) {
	    		args.add(declaration.accept(this));
	    	}
	    	for (Node type : f.returns) {
	    		returns.add(type.accept(this));
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
    	
    	if (type.args.size() != args.size()) { return false; }
    	if (type.returns.size() != returns.size()) { return false; }
    	
    	for (int i = 0; i < args.size(); i++) {
    		if (!type.args.get(i).equals(args.get(i))) {
    			return false;
    		}
    	}
    	
    	for (int i = 0; i < returns.size(); i++) {
    		if (!type.returns.get(i).equals(returns.get(i))) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    @Override
    public int hashCode() {
    	int hash = 1;
    	for (int i = 0; i < args.size(); i++) {
    		hash *= args.get(i).hashCode();
    	}
    	
    	for (int i = 0; i < returns.size(); i++) {
    		hash *= returns.get(i).hashCode();
    	}
    	return hash;
    }
}