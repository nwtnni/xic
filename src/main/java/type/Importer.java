package type;

import ast.*;
import parser.IXiParser;
import type.TypeException.Kind;
import xic.XicException;

/**
 * Subclass of {@link TypeChecker} which type checks top-level
 * declarations of both interface (.ixi) and implementation (.xi)
 * files.
 * 
 * Importer recursively visits {@code use} dependencies. Conceptually,
 * this can be broken down into three phases:
 * 
 * 1) For each dependency, search for the .ixi file and parse into AST
 * 2) For each function in the AST, add it to the {@link FnContext} if it is unique.
 * 3) For each dependency, merge the FnContexts back into the original AST, checking
 *    that shadowed functions have the same types.
 *    
 * Additionally, step 2 can be broken down into two passes:
 * 
 * 1) Add each function's types to the FnContext
 * 2) Check all function arguments for shadowing against the top-level context
 */
public class Importer extends TypeChecker {

	/**
	 * Factory method to resolve dependencies in an AST into a FnContext.
	 * 
	 * @param lib Directory to search for .ixi files
	 * @param ast Parsed source AST to extract use dependencies from
	 * @return Top-level function declarations in FnContext form
	 * @throws XicException if any functions are illegally shadowed
	 */
    public static FnContext resolve(String lib, Node ast) throws XicException {
    	Importer resolver = new Importer(lib);
    	ast.accept(resolver);
    	return resolver.fns;
    }
    
    /**
     * Directory to search for .ixi files
     */
    private String lib;
    
    /**
     * True when this Importer is on its first or second pass through the Fn nodes
     */
    private boolean populate;
    
    /**
     * Creates a new Importer that will search the given directory
     * 
     * @param lib Directory to search for .ixi files
     * @throws XicException if importing failed (e.g. invalid .ixi file
     *   		or illegal function shadowing)
     */
    private Importer(String lib) throws XicException {
    	this.lib = lib;
    	this.populate = true;
    }
    
    /**
     * Visit a {@link ast.Program} and extract its top-level function
     * declarations into a {@link FnContext}
     */
    @Override
    public Type visit(Program p) throws XicException {
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
		
		if (p.isProgram()) {
    		for (Node use : p.uses) {
    			use.accept(this);
    		}
    	}

    	return null;
    }
    
    /**
     * Recursively visit dependencies.
     */
    @Override
    public Type visit(Use u) throws XicException {
    	Node ast = IXiParser.from(lib, u.file + ".ixi");
    	fns.merge(Importer.resolve(lib, ast));
    	return null;
    }
    
    /**
     * Add function type information to top-level context.
     */
    @Override
    public Type visit(Fn f) throws XicException {
    	if (!populate) {
			visit(f.args);
			visit(f.returns);
    	} else if (fns.contains(f.id)) {
    		throw new TypeException(Kind.DECLARATION_CONFLICT, f.location);
    	} else {
    		fns.add(f.id, FnType.from(f));
    	}
    	return null;
    }
}