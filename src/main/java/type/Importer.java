package type;

import java.util.Set;

import ast.*;
import parse.IXiParser;
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
    public static GlobalContext resolve(String lib, Node ast, Set<String> visited) throws XicException {
        Importer resolver = new Importer(lib, visited);
        ast.accept(resolver);
        return resolver.globalContext;
    }

    /**
     * Directory to search for .ixi files
     */
    private String lib;

    private Set<String> visited;

    /**
     * True when this Importer is on its first or second pass through the Fn nodes
     */
    private boolean populate;

    /**
     * Creates a new Importer that will search the given directory
     *
     * @param lib Directory to search for .ixi files
     * @throws XicException if importing failed (e.g. invalid .ixi file
     *           or illegal function shadowing)
     */
    private Importer(String lib, Set<String> visited) throws XicException {
        this.lib = lib;
        this.visited = visited;
        this.populate = true;
    }

    /**
     * Visit a {@link ast.XiProgram} and extract its top-level function
     * declarations into a {@link FnContext}
     */
    @Override
    public Type visit(XiProgram p) throws XicException {

        // Recursively visit dependencies first
        for (Node use : p.uses) {
            use.accept(this);
        }

        // First pass: populate top-level environment with function IDs
        for (Node n : p.body) {
            n.accept(this);
        }

        populate = false;

        // Second pass: check for shadowed arguments against top-level
        for (Node n : p.body) {
            n.accept(this);
        }

        return null;
    }

    @Override
    public Type visit(XiClass c) throws XicException {

        return null;
    }

    /**
     * Recursively visit dependencies.
     */
    @Override
    public Type visit(XiUse u) throws XicException {
        if (visited.contains(u.file)) return null;
        visited.add(u.file);
        Node ast = IXiParser.from(lib, u.file + ".ixi");
        globalContext.merge(Importer.resolve(lib, ast, visited));
        return null;
    }

    /**
     * Add function type information to top-level context.
     */
    @Override
    public Type visit(XiFn f) throws XicException {
        localContext.push();
        if (!populate) {
            visit(f.args);
            visit(f.returns);
        } else if (globalContext.contains(f.id)) {
            throw new TypeException(Kind.DECLARATION_CONFLICT, f.location);
        } else {
            globalContext.put(f.id, new FnType(visit(f.args), visit(f.returns)));
        }
        localContext.pop();
        return null;
    }
}
