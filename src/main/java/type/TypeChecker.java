package type;

import java.util.List;
import java.util.ArrayList;

import ast.*;
import type.TypeException.Kind;
import xic.XicException;
import xic.XicInternalException;

/**
 * Main type checking implementation. Recursively traverses the AST
 * and verifies typing rules at each node, as defined by the Xi Type
 * Specification. This implementation mutates the provided AST, decorating
 * each node with a Type field.
 */
public class TypeChecker extends ASTVisitor<Type> {

    /**
     * Factory method to type check the given AST and return the 
     * associated function context.
     * @param lib Directory to search for interface files
     * @param ast AST to typecheck
     * @throws XicException if a semantic error was found
     */
    public static FnContext check(String lib, Node ast) throws XicException {
        TypeChecker checker = new TypeChecker(lib, ast);
        ast.accept(checker);
        return checker.fns;
    }

    /**
     * Default constructor initializes empty contexts.
     */
    protected TypeChecker() {
        this.fns = new FnContext();
        this.types = new TypeContext();
        this.vars = new VarContext();
    }

    /**
     * This constructor initializes the FnContext with {@link Importer},
     * but leaves the other empty.
     * 
     * @param lib Directory to search for interface files
     * @param ast AST to resolve dependencies for
     * @throws XicException if a semantic error occurred while resolving dependencies
     */
    private TypeChecker(String lib, Node ast) throws XicException {
        this.fns = Importer.resolve(lib, ast);
        this.types = new TypeContext();
        this.vars = new VarContext();
    }

    /**
     * Associated function context.
     */
    protected FnContext fns;

    /**
     * Associated variable context.
     */
    protected VarContext vars;
    
    /**
     * Associated type context.
     */
    private TypeContext types;
    
    /**
     * The current value of rho, the expected return
     * type for the current function in scope, as defined
     * in the type specification.
     */
    private Type returns;


    /*
     * Visitor Methods ---------------------------------------------------------------------
     */

    /**
     * Returns a list of types from visiting a list of nodes.
     */
    public List<Type> visit(List<Node> nodes) throws XicException {
        List<Type> types = new ArrayList<>();
        for (Node n : nodes) {
            types.add(n.accept(this));
        }
        return types;
    }

    /*
     * Top-level AST nodes
     */
    
    /**
     * A program is valid if all of its top-level declarations
     * are valid. Use statements and top-level declarations 
     * are checked by {@link Importer},
     * while function bodies are checked by this class.
     * 
     * @returns {@link TypeCheck.UNIT} if program is valid
     * @throws XicException if program has semantic errors
     */
    public Type visit(XiProgram p) throws XicException {
        for (Node fn : p.body) {
            fn.accept(this);
        }
        p.type = Type.UNIT;
        return p.type;
    }

    // We do not need to typecheck XiUse, (visitor will return null) TODO
    public Type visit(XiUse u) throws XicException {
        throw new RuntimeException();
    }

    //PA7 TODO
    public Type visit(XiClass c) throws XicException {
        throw new RuntimeException();
    }

    /**
     * A function is valid if none of its arguments
     * shadow anything in the context, and its block is
     * void if it has return types.
     * 
     * @returns {@link TypeCheck.UNIT} is function is valid
     * @throws XicException if function has semantic errors
     */
    public Type visit(XiFn f) throws XicException {
        vars.push();
        FnType fnType = fns.lookup(f.id);
        if (fnType == null) {
            // Internal error occurred; should never happen
            throw XicInternalException.runtime("Function not found. Fix Importer.");
        }

        visit(f.args);
        returns = fnType.returns;
        Type ft = f.block.accept(this);

        if (f.isFn() && !ft.equals(Type.VOID)) {
            throw new TypeException(Kind.CONTROL_FLOW, f.location);
        }

        vars.pop();
        f.type = Type.UNIT;
        return f.type;
    }

    // PA7 TODO
    // Need to handle case with assignment
    public Type visit(XiGlobal g) throws XicException{
        throw new RuntimeException();
    }

    /*
     * Statement nodes
     */

    /**
     * An assignment is valid if each type on the RHS is a subtype of the
     * corresponding type on the LHS, and the number of types is matched.
     * 
     * Additionally, a procedure cannot be assigned to anything, and only
     * function calls can have wildcards on the LHS.
     * 
     * @returns {@link TypeCheck.UNIT} if valid
     * @throws XicException if invalid assignment
     */
    public Type visit(XiAssign a) throws XicException {
        Type rt = a.rhs.accept(this);
        Type lt = Type.tupleFromList(visit(a.lhs));

        if (!types.isSubType(rt, lt)) {
            throw new TypeException(Kind.MISMATCHED_ASSIGN, a.location);
        }

        if (lt.equals(Type.UNIT) && !(a.rhs instanceof XiCall)) {
            throw new TypeException(Kind.INVALID_WILDCARD, a.location);
        }

        a.type = Type.UNIT;
        return a.type;
    }

    /**
     * A block is valid if each statement is valid, and no statement before the
     * last one is type {@link TypeCheck.VOID}.
     * 
     * @returns The type of the last statement
     * @throws XicException if invalid
     */
    public Type visit(XiBlock b) throws XicException {
        b.type = Type.UNIT;
        vars.push();
        int size = b.statements.size();

        for (int i = 0; i < size; i++) {

            Node s = b.statements.get(i);
            Type st = s.accept(this);

            // Unused function result
            if (!st.equals(Type.VOID) && !st.equals(Type.UNIT) && s instanceof XiCall) {
                throw new TypeException(Kind.UNUSED_FUNCTION, b.statements.get(i).location);
            }

            // Unreachable code
            if (i < size - 1 && st.equals(Type.VOID)) {
                throw new TypeException(Kind.UNREACHABLE, b.statements.get(i + 1).location);
            } else {
                b.type = st.equals(Type.VOID) ? Type.VOID : Type.UNIT;
            }
        }
        vars.pop();
        return b.type;
    }

    // PA7
    public Type visit(XiBreak b) throws XicException {
        return Type.VOID;
    }
    
    /**
     * A declaration is valid if it doesn't shadow anything in the context.
     * 
     * @returns typeof(declaration) if valid
     * @throws XicException if a conflict was found
     */
    public Type visit(XiDeclr d) throws XicException {
        if (d.isUnderscore()) {
            d.type = Type.UNIT;
        } else if (vars.contains(d.id) || fns.contains(d.id)) {
            throw new TypeException(Kind.DECLARATION_CONFLICT, d.location);
        } else {
            d.type = d.xiType.accept(this);
            vars.add(d.id, d.type);
        }
        return d.type;
    }

    /**
     * An if statement is valid if its guard is {@link TypeCheck.BOOL} and its block is valid.
     * 
     * @returns If both blocks are {@link TypeCheck.VOID}, then Type.VOID, otherwise Type.UNIT
     * @throws XicException if invalid
     */
    public Type visit(XiIf i) throws XicException {
        if (!i.guard.accept(this).equals(Type.BOOL)) {
            throw new TypeException(Kind.INVALID_GUARD, i.guard.location);
        }

        // Check if the statement is followed by a single statement
        if (!(i.block instanceof XiBlock)) {
            i.block = new XiBlock(i.block.location, i.block);
        }
        Type it = i.block.accept(this);
        
        Type et = null;
        if (i.hasElse()) {
            if (!(i.elseBlock instanceof XiBlock)) {
                i.elseBlock = new XiBlock(i.elseBlock.location, i.elseBlock);
            }
            et = i.elseBlock.accept(this);
        }

        if (et != null && it.equals(Type.VOID) && et.equals(Type.VOID)) {
            i.type = Type.VOID;
        } else {
            i.type = Type.UNIT;
        }
        return i.type;
    }

    /**
     * A return is valid if its type matches {@link TypeChecker#returns}
     * 
     * @returns {@link TypeCheck.VOID} if return type matches {@link TypeChecker#returns}
     * @throws XicException if return type doesn't match
     */
    public Type visit(XiReturn r) throws XicException {
        if (r.hasValues()) {
            Type value = Type.tupleFromList(visit(r.values));
            for (Node n : r.values) {
                if (n instanceof XiCall) {
                    if (n.type.kind.equals(Type.Kind.TUPLE)) {
                        throw new TypeException(Kind.MISMATCHED_RETURN, r.location);
                    }
                }
            }
            if (!value.equals(Type.UNIT) && returns.equals(value)) {
                r.type = Type.VOID;
                return r.type;
            }
        } else if (returns.equals(Type.UNIT)) {
            r.type = Type.VOID;
            return r.type;
        }
        throw new TypeException(Kind.MISMATCHED_RETURN, r.location);
    }

    /**
     * A while statement is valid if its guard is {@link TypeCheck.BOOL} and its block is valid.
     * 
     * @returns {@link TypeCheck.UNIT} if valid
     * @throws XicException if invalid
     */
    public Type visit(XiWhile w) throws XicException {
        if (!w.guard.accept(this).equals(Type.BOOL)) {
            throw new TypeException(Kind.INVALID_GUARD, w.guard.location);
        }

        if (!(w.block instanceof XiBlock)) {
            w.block = new XiBlock(w.block.location, w.block);
        }
        w.block.accept(this);
        w.type = Type.UNIT;
        return w.type;
    }

    /*
     * Expression nodes
     */

    /**
     * A binary operation is valid if the types of the operands and the operator match.
     */
    public Type visit(XiBinary b) throws XicException {
        Type lt = b.lhs.accept(this);
        Type rt = b.rhs.accept(this);

        if (!lt.equals(rt)) {
            throw new TypeException(Kind.MISMATCHED_BINARY, b.location);
        }

        if (lt.equals(Type.INT) && b.acceptsInt()) {
            if (b.returnsBool()) {
                b.type = Type.BOOL;
            } else {
                b.type = Type.INT;
            } 
        } else if (lt.equals(Type.BOOL) && b.acceptsBool()) {
            b.type = Type.BOOL;
        } else if (lt.kind.equals(Type.Kind.ARRAY) && b.acceptsList()) {
            if (b.returnsBool()) {
                b.type = Type.BOOL;
            } else {
                b.type = lt;
            }
        } else {
            throw new TypeException(Kind.INVALID_BIN_OP, b.location);
        }
        return b.type;
    }

    /**
     * A function call is valid if the arguments match the function's arguments.
     */
    public Type visit(XiCall c) throws XicException {
        if (c.id.equals("length")) {
            Type arg = c.args.get(0).accept(this);
            if (!arg.isArray()) {
                throw new TypeException(Kind.NOT_AN_ARRAY, c.location);
            }
            c.type = Type.INT;
            return c.type;
        } else {
            FnType fn = fns.lookup(c.id);
            if (fn == null) {
                throw new TypeException(Kind.SYMBOL_NOT_FOUND, c.location);
            }

            Type args = Type.listFromList(visit(c.args));
            if (args.equals(fn.args)) {
                c.type = fn.returns;
                return c.type;
            } else {
                throw new TypeException(Kind.INVALID_ARG_TYPES, c.location);
            }
        }
    }

    // PA7 TODO
    public Type visit(XiDot d) throws XicException {
        throw new RuntimeException();
    }

    /**
     * An array index is valid if the array child is {@link TypeCheck.Kind.ARRAY}, and the
     * index child is {@link TypeCheck.INT}
     */
    public Type visit(XiIndex i) throws XicException {
        Type it = i.index.accept(this);
        Type at = i.array.accept(this);

        if (!it.equals(Type.INT)) {
            throw new TypeException(Kind.INVALID_ARRAY_INDEX, i.index.location);
        } else if (at.kind != Type.Kind.ARRAY) {
            throw new TypeException(Kind.NOT_AN_ARRAY, i.array.location);
        } else {
            i.type = at.children.get(0);
            return i.type;
        }
    }

    // PA7 TODO
    public Type visit(XiNew n) {
        throw new RuntimeException();
    }

    // PA7 TODO
    public Type visit(XiThis t) throws XicException {
        throw new RuntimeException();
    }

    /**
     * A unary operator is valid if the type of the operator and operand match.
     * 
     * @returns The type of the operator if valid
     * @throws XicException if operator mismatch
     */
    public Type visit(XiUnary u) throws XicException {
        Type ut = u.child.accept(this);
        if (u.isLogical()) {
            if (ut.equals(Type.BOOL)) {
                u.type = Type.BOOL;
            } else {
                throw new TypeException(Kind.LNEG_ERROR, u.location);
            }
        } else {
            if (ut.equals(Type.INT)) {
                u.type = Type.INT;
            } else {
                throw new TypeException(Kind.NEG_ERROR, u.location);
            }
        }
        return u.type;
    }

    /**
     * A variable lookup is valid if the variable exists in the context.
     * 
     * @returns typeof(variable) if valid
     * @throws XicException if invalid
     */
    public Type visit(XiVar v) throws XicException {
        v.type = vars.lookup(v.id);
        if (v.type == null) {
            throw new TypeException(TypeException.Kind.SYMBOL_NOT_FOUND, v.location);
        }
        return v.type;
    }

    /*
     * Constant nodes
     */

    /**
     * A XiArray is valid if its children are the same type.
     * 
     * The 0-length array is polymorphic and has special type {@link TypeCheck.POLY},
     * which is equal to all array types.
     * 
     * @returns Array of child types
     * @throws XicException if invalid
     */
    public Type visit(XiArray a) throws XicException {
        if (a.values.size() == 0) {
            a.type = Type.POLY;
            return Type.POLY;
        } else {
            Type arrayType = a.values.get(0).accept(this);

            for (int i = 1; i < a.values.size(); i++) {
                Type elemType = a.values.get(i).accept(this);
                if (arrayType.isPoly()) {
                    arrayType = elemType;
                } else if (!arrayType.equals(elemType)) {
                    throw new TypeException(Kind.NOT_UNIFORM_ARRAY, a.location);
                }
            }

            // Iterate through elements again to coerce all types 
            for (int i = 0; i < a.values.size(); i++) {
                Type elemType = a.values.get(i).accept(this);
                elemType.equals(arrayType);
            }
            
            a.type = new Type(arrayType);
            return a.type;
        }
    }

    /**
     * A XiBool is always {@link TypeCheck.BOOL}
     * 
     * @returns {@link TypeCheck.BOOL}
     */
    public Type visit(XiBool b) {
        b.type = Type.BOOL;
        return b.type;
    }

    /**
     * A XiChar is always {@link TypeCheck.INT}
     * 
     * @returns {@link TypeCheck.INT}
     */
    public Type visit(XiChar c) {
        c.type = Type.INT;
        return c.type;
    }

    /**
     * A XiInt is always {@link TypeCheck.INT}
     * 
     * @returns {@link TypeCheck.INT}
     */
    public Type visit(XiInt i) {
        i.type = Type.INT;
        return i.type;
    }

    // PA7 TODO
    public Type visit(XiNull n) throws XicException {
        throw new RuntimeException();
    }

    /**
     * A XiString is always a {@link TypeCheck.Kind.ARRAY} of {@link TypeCheck.INT}
     * 
     * @returns Array of {@link TypeCheck.INT}
     */
    public Type visit(XiString s) {
        s.type = new Type(Type.INT);
        return s.type;
    }

    /**
     * A XiType is equal to its corresponding Type.
     * 
     * @returns Corresponding type
     * @throws XicException if array type with invalid size
     */
    public Type visit(XiType t) throws XicException {
        t.type = new Type(t);
        
        if (t.hasSize() && !t.size.accept(this).equals(Type.INT)) {
            throw new TypeException(Kind.INVALID_ARRAY_SIZE, t.size.location);
        }

        if (t.child != null) {
            t.child.accept(this);
        }

        return t.type;
    }
}
