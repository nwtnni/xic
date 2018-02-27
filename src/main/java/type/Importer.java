package type;

import ast.*;
import parser.IXiParser;
import xic.XicException;

public class Importer extends TypeCheck {
    
    public static FnContext resolve(String lib, Node ast) throws XicException {
    	Importer resolver = new Importer(lib);
    	ast.accept(resolver);
    	return resolver.fns;
    }
    
    private String lib;
    private boolean populate;
    
    private Importer(String lib) throws XicException {
    	this.lib = lib;
    	this.populate = true;
    }
    
    @Override
    public Type visit(Program p) throws XicException {
    	if (p.isProgram()) {
    		for (Node use : p.uses) {
    			use.accept(this);
    		}
    	}
    	
		// First pass: populate top-level environment with function IDs
    	for (Node fn : p.fns) {
    		fn.accept(this);
    	}
    	
    	populate = false;
    	
		// Second pass: check for shadowed arguments against top-level
    	for (Node fn : p.fns) {
    		vars.push();
    		fn.accept(this);
    		vars.pop();
    	}

    	return null;
    }
    
    @Override
    public Type visit(Use u) throws XicException {
    	Node ast = IXiParser.from(lib, u.file + ".ixi");
    	fns.merge(Importer.resolve(lib, ast));
    	return null;
    }
    
    @Override
    public Type visit(Fn f) throws XicException {
    	if (populate) {
        	fns.add(f.id, FnType.from(f));
    	} else {
        	f.args.accept(this);	
    	}
    	return null;
    }
}