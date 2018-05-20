package emit;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

import ast.*;
import interpret.Configuration;
import type.*;
import ir.*;
import ir.IRBinOp.OpType;
import ir.IRMem.MemType;
import xic.XicException;
import xic.XicInternalException;
import util.OrderedMap;
import util.Pair;

/**
 * Main decorated AST to IR translation implementation. Recursively
 * traverses the AST and constructs a new IR tree that represents the AST.
 */
public class Emitter extends ASTVisitor<IRNode> {

    /**
     * Factory method to generate IR from the given AST.
     * @param ast AST to generate into IR
     * @param context function context corresponding to the AST
     */
    public static Pair<IRCompUnit, ABIContext> emitIR(String unit, XiProgram ast, GlobalContext context) {
        IRFactory.reset();
        Emitter e = new Emitter(unit, context);
        try {
            return new Pair<>((IRCompUnit) ast.accept(e), e.context);
        } catch (XicException err) {
            throw XicInternalException.runtime("Failed to generate IR from valid AST: " + err.toPrint());
        }
    }

    public Emitter(String unit, GlobalContext context) {
        this.unit = unit;
        this.context = new ABIContext(context);
        this.currentClass = Optional.empty();
        this.currentLoop = new Stack<>();
    }

    /**
     * The compilation unit.
     */
    private String unit;

    /**
     * Associated function name to ABI name context.
     */
    protected ABIContext context;

    /**
     * Flag to see if currently visiting a class.
     */
    private Optional<ClassType> currentClass;

    /**
     * The current loop exit label.
     */
    private Stack<IRLabel> currentLoop;

    // Toggle inserting library functions
    private static final boolean INCLUDE_LIB = true;


    /**
     * Generate a conditional jump using C translations.
     */
    private IRStmt makeControlFlow(Node n, IRLabel trueL, IRLabel falseL) throws XicException {
        if (n instanceof XiBool) {
            XiBool b = (XiBool) n;
            if (b.value) {
                return Library.jump(trueL);
            } else {
                return Library.jump(falseL);
            }
        } else if (n instanceof XiBinary) {
            XiBinary b = (XiBinary) n;
            switch (b.kind) {
                case AND:
                    IRLabel andL = IRFactory.generateLabel("c_and");
                    return new IRSeq(
                        makeControlFlow(b.lhs, andL, falseL),
                        andL,
                        makeControlFlow(b.rhs, trueL, falseL)
                    );
                case OR:
                    IRLabel orL = IRFactory.generateLabel("c_or");
                    return new IRSeq(
                        makeControlFlow(b.lhs, trueL, orL),
                        orL,
                        makeControlFlow(b.rhs, trueL, falseL)
                    );
                default:
            }
        } else if (n instanceof XiUnary) {
            XiUnary u = (XiUnary) n;
            if (u.isLogical()) {
                makeControlFlow(u.child, falseL, trueL);
            }
        }
        return new IRSeq(
            new IRCJump((IRExpr) n.accept(this), trueL),
            Library.jump(falseL)
        );
    }

    /**
     * Generates init function for the class.
     */
    private IRFuncDecl init(XiClass cls) {
        GlobalContext gc = context.gc;
        String name = context.classInit(cls.id);
        ClassType ct = (ClassType) cls.type;
        ClassContext cc = context.gc.lookup(ct);

        IRFuncDecl fn = new IRFuncDecl(name, name, 0, 0);

        // Shuttle temp
        IRTemp t = IRFactory.generate();

        // Globals for class c
        IRExpr size = IRFactory.generateSize(cls.id, context);
        IRExpr vt = IRFactory.generateVT(cls.id, context);

        // Short circuit if already initialized
        IRLabel done = IRFactory.generateLabel(cls.id);
        fn.add(new IRMove(t, size));
        fn.add(new IRCJump(new IRBinOp(OpType.NEQ, Library.ZERO, t), done));

        // Recursively intialize parent with _I_init_parent
        if (cls.hasParent()) {
            fn.add(new IRExp(new IRCall(new IRName(context.classInit(cls.parent)), 0, List.of())));
        }

        // Initialize _I_size_name
        if (cls.hasParent()) {
            // Allocate size
            IRExpr parentSize = IRFactory.generateSize(cls.id, context);
            fn.add(new IRMove(t, parentSize));
        }
        fn.add(new IRMove(t, new IRBinOp(OpType.ADD, t, new IRConst(cc.numFields()))));
        fn.add(new IRMove(size, t));

        // Initialize _I_vt_name

        // Get vt address in c
        IRTemp c = IRFactory.generate("vt_" + cls.id);
        fn.add(new IRMove(c, new IRMem(vt, MemType.GLOBAL)));

        // Get parent vt address in p
        IRTemp p = null;
        if (cls.hasParent()) {
            IRExpr parentVT = IRFactory.generateVT(cls.parent, context);
            p = IRFactory.generate("vt_" + cls.parent);
            fn.add(new IRMove(p, new IRMem(parentVT, MemType.GLOBAL)));
        }

        // Copy or generate entries of vt
        int i = -1;
        OrderedMap<String, MethodType> methods = gc.lookupAllMethods(ct);

        System.out.println(methods.keyList());

        for (String method : methods.keyList()) {
            i++;
            if (method.matches("[0-9]+")) continue;

            IRConst offset = new IRConst(i * Configuration.WORD_SIZE);
            IRMem addr = new IRMem(new IRBinOp(OpType.ADD, c, offset), MemType.IMMUTABLE);

            // Copy inherited methods
            if (!cc.containsMethod(method)) {
                IRMem paddr = new IRMem(new IRBinOp(OpType.ADD, p, offset), MemType.IMMUTABLE);
                fn.add(new IRMove(addr, paddr));

            // Insert addresses for overriden and self defined methods
            } else {
                IRMem pointer = IRFactory.generateMethodAddr(method, ct, context);
                fn.add(new IRMove(t, pointer));
                fn.add(new IRMove(addr, t));
            }
        }

        fn.add(done);
        fn.add(new IRReturn());
        return fn;
    }

    /**
     * Generates code for dispatch to the field or method [name] from [obj] of type [type]
     */
    private IRExpr dispatch(IRExpr obj, String name, ClassType type) {
        GlobalContext gc = context.gc;
        ClassContext cc = gc.lookup(type);

        IRSeq setup = new IRSeq();

        // Field dispatch
        if (cc.containsField(name)) {
           //TODO plz

        }

        // Method dispatch
        if (cc.containsMethod(name)) {

            OrderedMap<String, MethodType> order = gc.lookupAllMethods(type);
            int offset = order.indexOf(name);

            // Access offset
            return new IRMem(new IRBinOp(OpType.ADD, new IRConst(offset * Configuration.WORD_SIZE), new IRMem(obj)));
        }

        throw new XicInternalException("Error in dispatch");
    }

    /*
     * Visitor methods ---------------------------------------------------------------------
     */

    /**
     * Returns a list of IRNodes from visiting a list of AST nodes.
     */
    @Override
    public List<IRNode> visit(List<Node> nodes) throws XicException {
        List<IRNode> ir = new ArrayList<>();
        for (Node n : nodes) {
            ir.add(n.accept(this));
        }
        return ir;
    }

    /*
     * Top-level AST nodes
     */

    @Override
    public IRNode visit(XiProgram p) throws XicException {
        IRCompUnit program = new IRCompUnit(this.unit);

        if (INCLUDE_LIB) {
            program.appendFunc(Library.xiArrayConcat());
            program.appendFunc(Library.xiDynamicAlloc());
        }

        // TODO: initialize globals and classes
        // - initialize global arrays
        // - initialize class size + vt
        List<IRStmt> globalInit = new ArrayList<>();
        List<IRStmt> classInit = new ArrayList<>();

        for (Node node : p.body) {

            // Visit globals
            if (node instanceof XiGlobal) {
                IRNode g = node.accept(this);
                if (node.type.isArray()) {
                    globalInit.add((IRStmt) g);
                }

                String name;
                Long value;
                Node stmt = ((XiGlobal) node).stmt;
                if (stmt instanceof XiAssign) {
                    name = context.mangleGlobal(((XiVar) ((XiAssign) stmt).lhs.get(0)).id);
                    if (((XiAssign) stmt).rhs instanceof XiInt) {
                        value = ((XiInt) ((XiAssign) stmt).rhs).value;
                    } else {
                        value = ((XiBool) ((XiAssign) stmt).rhs).value ? 1L : 0L; 
                    }
                } else if (stmt instanceof XiDeclr) {
                    name = context.mangleGlobal(((XiDeclr) stmt).id);
                    value = 0L;
                } else {
                    throw XicInternalException.runtime("Unknown global type");
                }

                program.globals().put(name, value);

            // Visit classes
            } else if (node instanceof XiClass) {
                XiClass c = (XiClass) node;
                currentClass = Optional.of((ClassType) c.type);

                // Create initialization function
                program.appendFunc(init(c));
                IRName name = new IRName(context.classInit(c.id));
                classInit.add(new IRExp(new IRCall(name, 0, List.of())));

                // Visit each method
                for (Node method : c.body) {
                    if (method instanceof XiFn) {
                        program.appendFunc((IRFuncDecl) method.accept(this));
                    }
                }

                currentClass = Optional.empty();
            // Visit functions
            } else if (node instanceof XiFn) {
                program.appendFunc((IRFuncDecl) node.accept(this));
            }
        }

        program.appendFunc(Library.generateInitFunc(classInit, globalInit));

        return program;
    }

    // Only need to initialize global arrays
    // PA7
    @Override
    public IRNode visit(XiGlobal g) throws XicException {
        return g.stmt.accept(this);
    }

    @Override
    public IRNode visit(XiFn f) throws XicException {
        IRSeq body = (IRSeq) f.block.accept(this);

        String name;
        int numArgs = f.args.size();
        int numRets = f.returns.size();

        // Inject receiver for object reference
        int argOffset = 0;
        if (currentClass.isPresent()) {
            body.add(0, new IRMove(Library.THIS, IRFactory.getArgument(0)));
            name = context.mangleMethod(f.id, currentClass.get());
            argOffset++;
            numArgs++;
        } else {
            name = context.mangleFunction(f.id);
        }

        // Inject temporary for multiple returns
        if (f.returns.size() > 2) {
            body.add(1, new IRMove(Library.CALLEE_MULT_RET, IRFactory.getArgument(argOffset)));
            argOffset++;
            numArgs++;
        }

        // Bind arguments to temps
        List<IRNode> args = visit(f.args);
        for (int i = 0; i < args.size(); i++) {
            body.add(i, new IRMove((IRExpr) args.get(i), IRFactory.getArgument(i + argOffset)));
        }

        // Insert empty return if needed
        if (body.size() == 0 || !(body.get(body.size() - 1) instanceof IRReturn)) {
            body.add(new IRReturn());
        }

        return new IRFuncDecl(f.id, name, numArgs, numRets, body);
    }

    /*
     * Statement nodes
     */

    @Override
    public IRNode visit(XiAssign a) throws XicException {
        List<IRNode> lhs = visit(a.lhs);
        IRExpr rhs = (IRExpr) a.rhs.accept(this);

        if (lhs.size() == 1) {

            // Assign to expression
            IRExpr var = (IRExpr) lhs.get(0);
            if (var != null) {
                return new IRMove(var, rhs);

            // Discard result if assign to underscore
            } else {
                return new IRExp(rhs);
            }
        }

        IRSeq stmts = new IRSeq();
        if (lhs.get(0) == null) {
            stmts.add(new IRExp(rhs));
        } else {
            stmts.add(new IRMove((IRExpr) lhs.get(0), rhs));
        }

        for (int i = 1; i < lhs.size(); i++) {
            IRNode n = lhs.get(i);
            if (n != null) {
                stmts.add(new IRMove((IRExpr) n, IRFactory.getReturn(i)));
            }
        }

        return stmts;
    }

    // PA7
    @Override
    public IRNode visit(XiBreak b) {
        return new IRJump(new IRName(currentLoop.peek()));
    }

    @Override
    public IRNode visit(XiBlock b) throws XicException {
        IRSeq stmts = new IRSeq();
        for (Node n : b.statements) {
            IRNode stmt = n.accept(this);
            // For procedures
            if (stmt instanceof IRCall) {
                stmts.add(new IRExp((IRCall) stmt));

            // Zero declarations
            } else if (stmt instanceof IRTemp) {
                stmts.add(new IRMove((IRTemp) stmt, new IRConst(0)));

            // Add other statements
            } else {
                stmts.add((IRStmt) stmt);
            }
        }

        return stmts;
    }

    @Override
    public IRNode visit(XiDeclr d) throws XicException {
        if (d.isUnderscore()) {
            return null;
        }

        IRExpr var;
        if (context.gc.contains(d.id)) {
            var = IRFactory.generateGlobal(d.id, context);
        } else {
            var = new IRTemp(d.id);
        }

        // Case for primitive and class
        if (d.type.isPrimitive() || d.type.isClass()) {
            return var;

        // Case for array
        } else if (d.type.isArray()) {
            // Case for array declaration with dimensions
            IRESeq arr = (IRESeq) d.xiType.accept(this);
            if (arr != null) {
                return new IRMove(var, arr);
            } else {
                return var;
            }

        // Can only have primitive, class, array
        } else {
            throw XicInternalException.runtime("Emitter: Visited non-local variable declaration.");
        }
    }

    @Override
    public IRNode visit(XiIf i) throws XicException {
        IRSeq stmts = new IRSeq();
        IRLabel trueL = IRFactory.generateLabel("if_t");
        IRLabel falseL = IRFactory.generateLabel("if_f");

        stmts.add(makeControlFlow(i.guard, trueL, falseL));
        stmts.add(trueL);
        stmts.add((IRStmt) i.block.accept(this));
        stmts.add(falseL);
        if (i.hasElse()) {
            IRLabel doneL = IRFactory.generateLabel("if_done");
            stmts.add(stmts.size() - 1, Library.jump(doneL));
            stmts.add((IRStmt) i.elseBlock.accept(this));
            stmts.add(doneL);
        }
        return stmts;
    }

    @Override
    public IRNode visit(XiReturn r) throws XicException {
        if (r.hasValues()) {
            List<IRExpr> values = new ArrayList<>();
            for (Node n : r.values) {
                values.add((IRExpr) n.accept(this));
            }
            return new IRReturn(values);
        }
        return new IRReturn();
    }

    // PA7
    @Override
    public IRNode visit(XiSeq s) throws XicException {
        throw XicInternalException.runtime("Found XiSeq. Check desugar.");
    }

    @Override
    public IRNode visit(XiWhile w) throws XicException {
        IRSeq stmts = new IRSeq();
        IRLabel headL = IRFactory.generateLabel("while_h");
        IRLabel trueL = IRFactory.generateLabel("while_t");
        IRLabel falseL = IRFactory.generateLabel("while_f");

        currentLoop.push(falseL);

        stmts.add(headL);
        stmts.add(makeControlFlow(w.guard, trueL, falseL));
        stmts.add(trueL);
        stmts.add((IRStmt) w.block.accept(this));
        stmts.add(Library.jump(headL));
        stmts.add(falseL);

        currentLoop.pop();

        return new IRSeq(stmts);
    }

    /*
     * Expression nodes
     */

    @Override
    public IRNode visit(XiBinary b) throws XicException {
        IRExpr left = (IRExpr) b.lhs.accept(this);
        IRExpr right = (IRExpr) b.rhs.accept(this);
        switch (b.kind) {
            case TIMES:
                return new IRBinOp(IRBinOp.OpType.MUL, left, right);
            case HIGH_TIMES:
                return new IRBinOp(IRBinOp.OpType.HMUL, left, right);
            case DIVISION:
                return new IRBinOp(IRBinOp.OpType.DIV, left, right);
            case MODULO:
                return new IRBinOp(IRBinOp.OpType.MOD, left, right);
            case PLUS:
                if (b.lhs.type.isArray()) {
                    return new IRCall(new IRName(Library.ARRAY_CONCAT), 1, left, right);
                }
                return new IRBinOp(IRBinOp.OpType.ADD, left, right);
            case MINUS:
                return new IRBinOp(IRBinOp.OpType.SUB, left, right);
            case LT:
                return new IRBinOp(IRBinOp.OpType.LT, left, right);
            case LE:
                return new IRBinOp(IRBinOp.OpType.LEQ, left, right);
            case GE:
                return new IRBinOp(IRBinOp.OpType.GEQ, left, right);
            case GT:
                return new IRBinOp(IRBinOp.OpType.GT, left, right);
            case EQ:
                return new IRBinOp(IRBinOp.OpType.EQ, left, right);
            case NE:
                return new IRBinOp(IRBinOp.OpType.NEQ, left, right);
            case AND:
                IRTemp andFlag = IRFactory.generate("and");
                IRLabel trueL = IRFactory.generateLabel("t");
                IRLabel falseL = IRFactory.generateLabel("f");
                return new IRESeq(
                    new IRSeq(
                        new IRMove(andFlag, new IRConst(0)),
                        makeControlFlow(b, trueL, falseL),
                        trueL,
                        new IRMove(andFlag, new IRConst(1)),
                        falseL
                    ),
                    andFlag
                );
            case OR:
                IRTemp orFlag = IRFactory.generate("or");
                trueL = IRFactory.generateLabel("t");
                falseL = IRFactory.generateLabel("f");
                return new IRESeq(
                    new IRSeq(
                        new IRMove(orFlag, new IRConst(1)),
                        makeControlFlow(b, trueL, falseL),
                        falseL,
                        new IRMove(orFlag, new IRConst(0)),
                        trueL
                    ),
                    orFlag
                );
        }
        // Unreachable
        assert false;
        return null;
    }

    @Override
    public IRNode visit(XiCall c) throws XicException {

        // Special case for length operator
        if (c.id instanceof XiVar && ((XiVar) c.id).id.equals("length")) {
            return Library.length((IRExpr) c.args.get(0).accept(this));
        }

        IRExpr target;
        String name;
        int numRets;
        List<IRExpr> argList = new ArrayList<>();

        // Method call requires injecting object reference
        if (c.id.type.isMethod()) {
            IRExpr obj;
            ClassType type;

            // Implicit this in a method
            if (c.id instanceof XiVar && currentClass.isPresent()) {
                name = ((XiVar) c.id).id;
                obj = Library.THIS;
                type = currentClass.get();

            // Off a dot access
            } else {
                XiDot d = (XiDot) c.id;
                name = ((XiVar) d.rhs).id;
                obj = (IRExpr) d.lhs.accept(this);
                type = (ClassType) d.lhs.type;
            }
            target = dispatch(obj, name, type);
            argList.add(obj);

            numRets = context.gc.lookup(type).lookupMethod(name).getNumRets();

        // Function call
        } else {
            name = ((XiVar) c.id).id;
            target = new IRName(context.mangleFunction(name));
            numRets = ((FnType) context.gc.lookup(name)).getNumRets();
        }

        // Inject dummy temp for multiple return if needed
        // Will be set by Tiler to appropriate memory address
        if (c.type.isTuple() && ((TupleType) c.type).size() > 2) {
            argList.add(Library.CALLER_MULT_RET);
        }

        // Get arguments
        for (Node n : c.getArgs()) {
            argList.add((IRExpr) n.accept(this));
        }

        return new IRCall(target, numRets, argList);
    }

    // PA7
    @Override
    public IRNode visit(XiDot d) throws XicException {
        IRExpr obj = (IRExpr) d.lhs.accept(this);
        IRTemp id = (IRTemp) d.rhs.accept(this);
        return dispatch(obj, id.name(), (ClassType) d.lhs.type);
    }

    /**
     * Returns an expression that
     *  - containing the memory address for an array access on LHS.
     *  - the value at the memory address for an array access on RHS.
     */
    @Override
    public IRNode visit(XiIndex i) throws XicException {
        IRSeq stmts = new IRSeq();
        IRLabel doneL = IRFactory.generateLabel("index_done");

        // Store array reference copy if not already a temp
        IRExpr pointer = (IRExpr) i.array.accept(this);
        if (!(pointer instanceof IRTemp)) {
            IRTemp temp = IRFactory.generate("array");
            stmts.add(new IRMove(temp, pointer));
            pointer = temp;
        }

        // Store index if not already a temp or constant
        IRExpr index = (IRExpr) i.index.accept(this);
        if (!(index instanceof IRTemp || index instanceof IRConst)) {
            IRTemp temp = IRFactory.generate("index");
            stmts.add(new IRMove(temp, index));
            index = temp;
        }

        // Check bounds
        IRLabel outOfBounds = IRFactory.generateLabel("out_of_bounds");
        stmts.add(new IRCJump(new IRBinOp(OpType.LT, index, Library.ZERO), outOfBounds));
        stmts.add(new IRCJump(new IRBinOp(OpType.GEQ, index, Library.length(pointer)), outOfBounds));
        stmts.add(Library.jump(doneL));
        stmts.add(outOfBounds);
        stmts.add(new IRExp(new IRCall(new IRName("_xi_out_of_bounds"), 0)));
        stmts.add(doneL);

        IRExpr byteShift = new IRBinOp(OpType.MUL, Library.WORD_SIZE, index);
        IRExpr addr = new IRBinOp(OpType.ADD, pointer, byteShift);
        return new IRMem(new IRESeq(stmts, addr));
    }

    // PA7
    @Override
    public IRNode visit(XiNew n) throws XicException {
        IRSeq setup = new IRSeq();

        IRTemp size = IRFactory.generate("size_" + n.name);
        IRTemp vt = IRFactory.generate("vt_" + n.name);
        IRTemp obj = IRFactory.generate("obj_" + n.name);

        // Allocate space
        setup.add(new IRMove(size, IRFactory.generateSize(n.name, context)));
        setup.add(new IRMove(size, new IRBinOp(OpType.MUL, Library.WORD_SIZE, size)));
        setup.add(new IRMove(obj, Library.alloc(size)));

        // Copy dispatch vector
        setup.add(new IRMove(vt, new IRMem(IRFactory.generateVT(n.name, context), MemType.GLOBAL)));
        setup.add(new IRMove(new IRMem(obj), vt));

        return new IRESeq(setup, obj);
    }

    @Override
    public IRNode visit(XiUnary u) throws XicException {
        IRExpr child = (IRExpr) u.child.accept(this);
        if (u.isLogical()) {
            return new IRBinOp(IRBinOp.OpType.XOR, new IRConst(1), child);
        } else {
            return new IRBinOp(IRBinOp.OpType.SUB, new IRConst(0), child);
        }
    }

    @Override
    public IRNode visit(XiVar v) throws XicException {

        // Global variables
        if (context.gc.contains(v.id)) {
            return IRFactory.generateGlobal(v.id, context);
        }

        // Ordinary temporary
        return new IRTemp(v.id);
    }

    /*
     * Constant nodes
     */

    @Override
    public IRNode visit(XiArray a) throws XicException {
        return Library.allocArray(visit(a.values));
    }

    @Override
    public IRNode visit(XiBool b) throws XicException {
        long value = b.value ? 1 : 0;
        return new IRConst(value);
    }

    @Override
    public IRNode visit(XiChar c) throws XicException {
        return new IRConst(c.value);
    }

    @Override
    public IRNode visit(XiInt i) throws XicException {
        return new IRConst(i.value);
    }

    // PA7
    @Override
    public IRNode visit(XiNull n) throws XicException {
        return new IRConst(0);
    }

    @Override
    public IRNode visit(XiString s) throws XicException {
        return Library.allocArray(s);
    }

    // PA7
    @Override
    public IRNode visit(XiThis t) throws XicException {
        return new IRTemp("this");
    }

    @Override
    public IRNode visit(XiType t) throws XicException {
        // Allocate memory for special case of syntactic sugar
        // for array declarations with dimensions specified
        if (t.hasSize()) {
            IRTemp size = IRFactory.generate("type_size");
            IRExpr sizeExpr =  (IRExpr) t.size.accept(this);
            IRESeq children = (IRESeq) t.child.accept(this);
            if (children == null) {
                IRSeq n = new IRSeq();
                n.add(new IRMove(size, sizeExpr));
                return new IRESeq(n, Library.allocArray(size));
            } else {
                IRSeq sizes = (IRSeq) children.stmt();
                IRExpr alloc = children.expr();
                sizes.add(0, new IRMove(size, sizeExpr));
                children.expr = Library.populate(size, alloc);
                return children;
            }
        } else {
            return null;
        }
    }

}
