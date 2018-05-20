package type;

import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

import ast.*;
import parse.IXiParser;
import xic.XicException;
import xic.XicInternalException;
import static type.TypeException.Kind.*;

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
    public static GlobalContext resolve(String lib, String file) throws XicException {
        return resolve(lib, file, new HashSet<>(Set.of(file)));
    }

    private static GlobalContext resolve(String lib, String file, Set<String> visited) throws XicException {
        try {
            Importer resolver = new Importer(lib, visited);
            Node ast = IXiParser.from(lib, file + ".ixi");
            ast.accept(resolver);
            return resolver.globalContext;
        } catch (XicInternalException io) {
            return new GlobalContext();
        }
    }

    /**
     * Directory to search for .ixi files
     */
    private String lib;
    private Set<String> visited;

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

        // First populate top-level elements: functions, and classes and their methods
        for (Node n : p.body) {

            // Populate top-level classes and methods while checking against interfaces
            if (n instanceof XiClass) {
                XiClass c = (XiClass) n;

                // Early return: classes must be unique across interface files
                if (globalContext.contains(c.id)) throw new TypeException(DECLARATION_CONFLICT, c.location);

                ClassContext cc = new ClassContext();
                ClassType ct = new ClassType(c.id);

                for (Node m : c.body) {
                    XiFn method = (XiFn) m;

                    // Early return: method names must be unique
                    if (cc.contains(method.id)) throw new TypeException(DECLARATION_CONFLICT, m.location);

                    // Otherwise add method type to class context
                    localContext.push();
                    MethodType mt = new MethodType(ct, visit(method.args), visit(method.returns));
                    cc.put(method.id, mt);
                    method.type = mt;
                    localContext.pop();
                }

                // Add class context to global context
                globalContext.put(c.id, cc);
                globalContext.setLocal(c.id);
                c.type = ct;

                // Lazily extend if necessary
                if (c.parent != null) globalContext.extend(ct, new ClassType(c.parent));
            }

            // Populate top-level functions
            else if (n instanceof XiFn) {
                XiFn f = (XiFn) n;

                // Early return: functions must be unique in a module
                if (globalContext.isLocal(f.id)) throw new TypeException(DECLARATION_CONFLICT, f.location);

                localContext.push();
                FnType type = new FnType(visit(f.args), visit(f.returns));
                f.type = type;
                localContext.pop();

                // Check interface conformance
                if (globalContext.contains(f.id)) {
                    if (!globalContext.lookup(f.id).equals(type)) throw new TypeException(MISMATCHED_INTERFACE, f.location);
                }

                globalContext.put(f.id, type);
                globalContext.setLocal(f.id);
            }
        }

        // Second pass: check everything
        this.initializing = false;
        for (Node n : p.body) {
            n.accept(this);
        }

        return null;
    }

    /**
     * Recursively visit dependencies.
     */
    @Override
    public Type visit(XiUse u) throws XicException {

        if (visited.contains(u.file)) return null;

        visited.add(u.file);
        if (!globalContext.merge(Importer.resolve(lib, u.file, visited))) {
            throw new TypeException(INCOMPATIBLE_USE, u.location);
        }

        return null;
    }

    @Override
    public Type visit(XiClass c) throws XicException {

        // Check each method for valid types
        for (Node n : c.body) {
            XiFn f = (XiFn) n;
            localContext.push();
            visit(f.args);
            visit(f.returns);
            localContext.pop();
        }

        ClassType ct = new ClassType(c.id);
        if (!globalContext.validate(ct)) throw new TypeException(INVALID_OVERRIDE, c.location);
        return null;
    }

    /**
     * Add function type information to top-level context.
     */
    @Override
    public Type visit(XiFn f) throws XicException {
        localContext.push();
        visit(f.args);
        visit(f.returns);
        localContext.pop();
        return null;
    }
}
