package type;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import ast.*;
import xic.XicException;
import xic.XicInternalException;

import static type.TypeException.Kind.*;

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

    protected GlobalContext globalContext;

    private ClassType inside;

    /**
     * The current value of rho, the expected return
     * type for the current function in scope, as defined
     * in the type specification.
     */
    private List<Type> returns;

    private LocalContext localContext;

    private boolean allSubclass(List<Type> subs, List<Type> supers) {
        for (int i = 0; i < subs.size(); i++) {
            Type sub = subs.get(i);
            Type sup = supers.get(i);

            // Equal classes can always be passed
            if (sub.equals(sup)) continue;

            // Null subclasses objects and arrays
            if (sub.isNull() && (sup.isClass() || sup.isArray())) continue;

            // Polymorphic arrays can be passed as any array
            if (sub.isPoly() && sup.isArray()) continue;

            // Otherwise must check class hierarchy
            if (sub.isClass() && sup.isClass() && globalContext.isSubclass((ClassType) sub, (ClassType) sup)) continue;

            return false;
        }
        return true;
    }

    /*
     * Visitor Methods ---------------------------------------------------------------------
     */

    /**
     * Returns a list of types from visiting a list of nodes.
     */
    @Override
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
    @Override
    public Type visit(XiProgram p) throws XicException {

        // First pass to populate global variables
        for (Node n : p.body) {
            if (n instanceof XiGlobal) {

                // Required try/catch if an array global uses other globals e.g.
                // arr: int[length];
                // length: int
                try { n.accept(this); } catch (XicException e) {}
            }
        }

        // Second pass to finish populating global variables
        for (Node n : p.body) {
            if (n instanceof XiGlobal && n.type == null) n.accept(this);
        }

        // Third pass to populate function and method bodies
        for (Node n : p.body) {
            if (!(n instanceof XiGlobal)) n.accept(this);
        }

        p.type = UnitType.UNIT;
        return p.type;
    }

    @Override
    public Type visit(XiGlobal g) throws XicException {

        // Declaration; must be either array or class
        if (g.stmt instanceof XiDeclr) {
            XiDeclr declr = (XiDeclr) g.stmt;

            if (globalContext.contains(declr.id)) {
                throw new TypeException(DECLARATION_CONFLICT, g.location);
            }

            globalContext.put(declr.id, (GlobalType) declr.xiType.accept(this));
            g.type = UnitType.UNIT;
            return g.type;
        }

        // Literal assisgnment; must be XiInt or XiBool
        if (g.stmt instanceof XiAssign) {
            XiAssign assign = (XiAssign) g.stmt;
            XiVar var = (XiVar) assign.lhs.get(0);

            if (globalContext.contains(var.id)) {
                throw new TypeException(DECLARATION_CONFLICT, g.location);
            }

            var.type = assign.rhs.accept(this);
            globalContext.put(var.id, (GlobalType) var.type);
            g.type = UnitType.UNIT;
            return g.type;
        }

        throw new XicInternalException("XiGlobal must be XiDeclr or XiAssign");
    }

    @Override
    public Type visit(XiClass c) throws XicException {

        // First pass to populate class context
        ClassContext cc = new ClassContext();
        ClassType ct = new ClassType(c.id);

        for (Node n : c.body) {

            if (n instanceof XiDeclr) {
                XiDeclr field = (XiDeclr) n;

                cc.put(
                    field.id,
                    (FieldType) field.xiType.accept(this)
                );
            }

            if (n instanceof XiFn) {
                XiFn method = (XiFn) n;
                cc.put(
                    method.id,
                    new MethodType(ct, visit(method.args), visit(method.returns))
                );
            }
        }

        // Validate context with global context from importing
        if (globalContext.contains(ct)) {
            ClassContext imported = globalContext.lookup(ct);
            if (!imported.merge(cc)) throw new TypeException(MISMATCHED_INTERFACE, c.location);
        }

        // Populate class method bodies
        inside = new ClassType(c.id);
        for (Node n : c.body) {
            if (n instanceof XiFn) n.accept(this);
        }
        inside = null;
    }

    /**
     * A function is valid if none of its arguments
     * shadow anything in the context, and its block is
     * void if it has return types.
     *
     * @returns {@link TypeCheck.UNIT} is function is valid
     * @throws XicException if function has semantic errors
     */
    @Override
    public Type visit(XiFn f) throws XicException {
        localContext.push();
        FnType fnType = (FnType) globalContext.lookup(f.id);

        visit(f.args);
        returns = fnType.getReturns();
        Type ft = f.block.accept(this);

        if (f.isFn() && !ft.isVoid()) {
            throw new TypeException(CONTROL_FLOW, f.location);
        }

        localContext.pop();
        f.type = UnitType.UNIT;
        return f.type;
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
    @Override
    public Type visit(XiAssign a) throws XicException {

        Type right = a.rhs.accept(this);
        List<Type> lt = visit(a.lhs);

        // Only function calls can use wildcards
        if (lt.stream().anyMatch(t -> t.isUnit()) && !(a.rhs instanceof XiCall)) {
            throw new TypeException(INVALID_WILDCARD, a.location);
        }

        // Procedures are disallowed in an assign
        if (a.rhs instanceof XiCall && right.isUnit()) {
            throw new TypeException(MISMATCHED_ASSIGN, a.location);
        }

        // Right hand side must subtype left hand side
        List<Type> rt = right.isTuple() ? ((TupleType) right).getTuple() : List.of(right);

        if (lt.size() != rt.size() || !allSubclass(rt, lt)) {
            throw new TypeException(MISMATCHED_ASSIGN, a.location);
        }

        a.type = UnitType.UNIT;
        return a.type;
    }

    /**
     * A block is valid if each statement is valid, and no statement before the
     * last one is type {@link TypeCheck.VOID}.
     *
     * @returns The type of the last statement
     * @throws XicException if invalid
     */
    @Override
    public Type visit(XiBlock b) throws XicException {

        b.type = UnitType.UNIT;
        localContext.push();
        int size = b.statements.size();

        for (int i = 0; i < size; i++) {

            Node s = b.statements.get(i);
            Type st = s.accept(this);

            // Unused function result
            if (!st.isVoid() && !st.isUnit() && s instanceof XiCall) {
                throw new TypeException(UNUSED_FUNCTION, b.statements.get(i).location);
            }

            // Unreachable code
            if (i < size - 1 && st.isVoid()) {
                throw new TypeException(UNREACHABLE, b.statements.get(i + 1).location);
            } else {
                b.type = st.isVoid() ? VoidType.VOID : UnitType.UNIT;
            }
        }

        localContext.pop();
        return b.type;
    }

    /**
     * PA7: A break has type void.
     *
     * @returns {@link TypeCheck.VOID}
     */
    @Override
    public Type visit(XiBreak b) throws XicException {
        return VoidType.VOID;
    }

    /**
     * A declaration is valid if it doesn't shadow anything in the context.
     *
     * @returns typeof(declaration) if valid
     * @throws XicException if a conflict was found
     */
    @Override
    public Type visit(XiDeclr d) throws XicException {

        // Early return: underscore binds nothing
        if (d.isUnderscore()) {
            d.type = UnitType.UNIT;
            return d.type;
        }

        // In this pass, we only check local variables
        // We allow shadowing against class fields and methods, which can be resolved with the this keyword
        if (localContext.contains(d.id) || globalContext.contains(d.id)) throw new TypeException(DECLARATION_CONFLICT, d.location);

        d.type = d.xiType.accept(this);
        localContext.add(d.id, (FieldType) d.type);
        return d.type;
    }

    /**
     * An if statement is valid if its guard is {@link TypeCheck.BOOL} and its block is valid.
     *
     * @returns If both blocks are {@link TypeCheck.VOID}, then Type.VOID, otherwise Type.UNIT
     * @throws XicException if invalid
     */
    @Override
    public Type visit(XiIf i) throws XicException {

        if (!i.guard.accept(this).isBool()) throw new TypeException(INVALID_GUARD, i.guard.location);

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

        if (et != null && it.isVoid() && et.isVoid()) {
            i.type = VoidType.VOID;
        } else {
            i.type = UnitType.UNIT;
        }
        return i.type;
    }

    /**
     * A return is valid if its type matches {@link TypeChecker#returns}
     *
     * @returns {@link TypeCheck.VOID} if return type matches {@link TypeChecker#returns}
     * @throws XicException if return type doesn't match
     */
    @Override
    public Type visit(XiReturn r) throws XicException {

        // Returning values
        if (r.hasValues()) {
            List<Type> values = visit(r.values);

            for (Node n : r.values) {
                if (n instanceof XiCall && n.type.isTuple()) {
                    throw new TypeException(MISMATCHED_RETURN, r.location);
                }
            }

            if (values.size() != returns.size() || !allSubclass(values, returns)) {
                throw new TypeException(MISMATCHED_RETURN, r.location);
            }

            r.type = VoidType.VOID;
            return r.type;
        }

        // Procedure; no values returned
        else if (returns.isEmpty()) {
            r.type = VoidType.VOID;
            return r.type;
        }

        throw new TypeException(MISMATCHED_RETURN, r.location);
    }

    // Should be removed in desugaring
    @Override
    public Type visit(XiSeq s) throws XicException {
        throw XicInternalException.runtime("Did not desugar AST.");
    }

    /**
     * A while statement is valid if its guard is {@link TypeCheck.BOOL} and its block is valid.
     *
     * @returns {@link TypeCheck.UNIT} if valid
     * @throws XicException if invalid
     */
    @Override
    public Type visit(XiWhile w) throws XicException {
        if (!w.guard.accept(this).isBool()) {
            throw new TypeException(INVALID_GUARD, w.guard.location);
        }

        if (!(w.block instanceof XiBlock)) {
            w.block = new XiBlock(w.block.location, w.block);
        }

        w.block.accept(this);
        w.type = UnitType.UNIT;
        return w.type;
    }

    /*
     * Expression nodes
     */

    /**
     * A binary operation is valid if the types of the operands and the operator match.
     */
    @Override
    public Type visit(XiBinary b) throws XicException {
        Type lt = b.lhs.accept(this);
        Type rt = b.rhs.accept(this);

        // Special cases: class comparisons and array comparisons
        if (b.kind == XiBinary.Kind.EQ || b.kind == XiBinary.Kind.NE) {

            // Class comparisons are privately scoped, except when comparing to null
            if ((lt.isClass() && (lt.equals(inside) || rt.isNull()))
            ||  (rt.isClass() && (rt.equals(inside) || lt.isNull()))
            ||  (lt.isClass() && lt.equals(inside) && rt.isClass() && rt.equals(inside))) {
                b.type = BoolType.BOOL;
                return b.type;
            }

            // Arrays can always be compared to null
            if ((lt.isArray() && rt.isNull())
            ||  (rt.isArray() && lt.isNull())
            ||  (lt.isNull()  && rt.isNull())) {
                b.type = BoolType.BOOL;
                return b.type;
            }
        }

        if (!lt.equals(rt)) {
            throw new TypeException(MISMATCHED_BINARY, b.location);
        }

        if (lt.isInt() && b.acceptsInt()) {
            b.type = b.returnsBool() ? BoolType.BOOL : IntType.INT;
        } else if (lt.isBool() && b.acceptsBool()) {
            b.type = BoolType.BOOL;
        } else if (lt.isArray() && b.acceptsList()) {
            b.type = lt;
        } else {
            throw new TypeException(INVALID_BIN_OP, b.location);
        }
        return b.type;
    }

    /**
     * A function call is valid if the arguments match the function's arguments.
     */
    @Override
    public Type visit(XiCall c) throws XicException {

        // Early return: builtin length function
        if (c.id instanceof XiVar && ((XiVar) c.id).id.equals("length")) {
            XiVar fn = (XiVar) c.id;
            if (c.args.size() != 1 || !c.args.get(0).accept(this).isArray()) {
                throw new TypeException(NOT_AN_ARRAY, c.location);
            }
            c.type = IntType.INT;
            return c.type;
        }

        FnType ft = null;

        // Must be function call
        if (c.id instanceof XiVar) {
            GlobalType type = globalContext.lookup(((XiVar) c.id).id);
            c.id.type = type;
            if (type == null || !type.isFn()) throw new TypeException(SYMBOL_NOT_FOUND, c.location);
            ft = (FnType) type;
        }

        // Must be method call
        else {
            Type type = c.id.accept(this);
            if (!type.isMethod()) throw new TypeException(NOT_A_METHOD, c.location);
            ft = (MethodType) type;
        }

        // Check parameter passing for both function and method
        List<Type> caller = visit(c.args);
        List<Type> called = ft.getArgs();

        if (caller.size() != called.size()) throw new TypeException(INVALID_ARG_TYPES, c.location);
        if (!allSubclass(caller, called)) throw new TypeException(INVALID_ARG_TYPES, c.location);

        c.type = (ft.getReturns().size() == 0) ? UnitType.UNIT : new TupleType(ft.getReturns());
        return c.type;
    }

    @Override
    public Type visit(XiDot d) throws XicException {

        // Must be XiVar, or else internal error
        XiVar var = (XiVar) d.rhs;

        // LHS of dot operator must be class
        Type lt = d.lhs.accept(this);
        if (!lt.isClass()) throw new TypeException(INVALID_DOT, d.lhs.location);

        // Class must be bound in global context
        ClassType ct = (ClassType) lt;
        if (!globalContext.contains(ct)) throw new TypeException(UNBOUND_CLASS, d.lhs.location);

        // RHS field or method must be bound in class
        ClassContext classContext = globalContext.lookup(ct);
        if (!classContext.contains(var.id)) throw new TypeException(UNBOUND_FIELD, d.rhs.location);

        d.rhs.type = classContext.lookup(var.id);
        d.type = d.rhs.type;
        return d.type;
    }

    /**
     * An array index is valid if the array child is {@link TypeCheck.Kind.ARRAY}, and the
     * index child is {@link TypeCheck.INT}
     */
    @Override
    public Type visit(XiIndex i) throws XicException {
        Type it = i.index.accept(this);
        Type at = i.array.accept(this);

        if (!it.isInt()) throw new TypeException(INVALID_ARRAY_INDEX, i.index.location);
        if (!at.isArray()) throw new TypeException(NOT_AN_ARRAY, i.array.location);

        ArrayType a = (ArrayType) at;
        i.type = a.getChild();
        return i.type;
    }

    @Override
    public Type visit(XiNew n) throws XicException {
        ClassType ct = new ClassType(n.name);
        if (inside == null || !inside.equals(ct)) throw new TypeException(UNBOUND_NEW, n.location);
        n.type = ct;
        return n.type;
    }

    /**
     * A unary operator is valid if the type of the operator and operand match.
     *
     * @returns The type of the operator if valid
     * @throws XicException if operator mismatch
     */
    @Override
    public Type visit(XiUnary u) throws XicException {
        Type ut = u.child.accept(this);

        if (u.isLogical() && !ut.isBool()) throw new TypeException(LNEG_ERROR, u.location);
        if (!u.isLogical() && !ut.isInt()) throw new TypeException(NEG_ERROR, u.location);

        u.type = ut;
        return u.type;
    }

    /**
     * A variable lookup is valid if the variable exists in the context.
     *
     * @returns typeof(variable) if valid
     * @throws XicException if invalid
     */
    @Override
    public Type visit(XiVar v) throws XicException {

        // Early return: local context contains symbol
        if (localContext.contains(v.id)) {
            v.type = localContext.lookup(v.id);
            return v.type;
        }

        // Early return: class context contains symbol
        if (inside != null && globalContext.lookup(inside).containsField(v.id)) {
            v.type = globalContext.lookup(inside).lookupField(v.id);
            return v.type;
        }

        // Early return: symbol not found
        if (!globalContext.contains(v.id) || !globalContext.lookup(v.id).isField()) {
            throw new TypeException(SYMBOL_NOT_FOUND, v.location);
        }

        // Must be valid global symbol
        v.type = globalContext.lookup(v.id);
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
    @Override
    public Type visit(XiArray a) throws XicException {

        List<Type> types = a.values.stream()
            .map(elem -> elem.accept(this))
            .collect(Collectors.toList());

        // Early return: empty literal array
        if (a.values.size() == 0) {
            a.type = PolyType.POLY;
            return a.type;
        }

        // Early return: entirely polymorphic array
        if (types.stream().allMatch(elem -> elem.isPoly())) {
            a.type = PolyType.POLY;
            return a.type;
        }

        // Otherwise must be at least one non-polymorphic element
        Type reference = types.stream()
            .filter(elem -> !elem.isPoly())
            .findFirst()
            .get();

        for (int i = 0; i < types.size(); i++) {

            Type type = types.get(i);

            // Coerce polymorphic types
            if (type.isPoly()) {
                a.values.get(i).type = reference;
            } else if (!type.equals(reference)) {
                throw new TypeException(NOT_UNIFORM_ARRAY, a.values.get(i).location);
            }
        }

        a.type = new ArrayType(reference);
        return a.type;
    }

    /**
     * A XiBool is always {@link TypeCheck.BOOL}
     *
     * @returns {@link TypeCheck.BOOL}
     */
    @Override
    public Type visit(XiBool b) {
        b.type = BoolType.BOOL;
        return b.type;
    }

    /**
     * A XiChar is always {@link TypeCheck.INT}
     *
     * @returns {@link TypeCheck.INT}
     */
    @Override
    public Type visit(XiChar c) {
        c.type = IntType.INT;
        return c.type;
    }

    /**
     * A XiInt is always {@link TypeCheck.INT}
     *
     * @returns {@link TypeCheck.INT}
     */
    @Override
    public Type visit(XiInt i) {
        i.type = IntType.INT;
        return i.type;
    }

    @Override
    public Type visit(XiNull n) throws XicException {
        n.type = NullType.NULL;
        return n.type;
    }

    /**
     * A XiString is always a {@link TypeCheck.Kind.ARRAY} of {@link TypeCheck.INT}
     *
     * @returns Array of {@link TypeCheck.INT}
     */
    @Override
    public Type visit(XiString s) {
        s.type = new ArrayType(IntType.INT);
        return s.type;
    }

    @Override
    public Type visit(XiThis t) throws XicException {
        if (inside == null) throw new TypeException(UNBOUND_THIS, t.location);
        t.type = inside;
        return t.type;
    }

    /**
     * A XiType is equal to its corresponding Type.
     *
     * @returns Corresponding type
     * @throws XicException if array type with invalid size
     */
    @Override
    public Type visit(XiType t) throws XicException {

        // Check for size expression
        if (t.hasSize() && !t.size.accept(this).equals(IntType.INT)) {
            throw new TypeException(INVALID_ARRAY_SIZE, t.size.location);
        }

        // Check for array type
        if (t.isArray()) {
            t.type = new ArrayType(t.child.accept(this));
            return t.type;
        }

        // Must be primitive or class
        switch (t.id) {
        case "int":
            t.type = IntType.INT;
            break;
        case "bool":
            t.type = BoolType.BOOL;
            break;
        default:
            t.type = new ClassType(t.id);
            break;
        }

        return t.type;
    }
}
