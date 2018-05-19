package emit;

import java.util.List;
import java.util.ArrayList;

import ast.*;
import type.FnContext;
import ir.*;
import ir.IRBinOp.OpType;
import ir.IRMem.MemType;
import xic.XicException;
import xic.XicInternalException;
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
    public static Pair<IRCompUnit, ABIContext> emitIR(String unit, XiProgram ast, FnContext context) {
        IRTempFactory.reset();
        Emitter e = new Emitter(unit, context);
        try {
            return new Pair<>((IRCompUnit) ast.accept(e), e.context);
        } catch (XicException err) {
            throw XicInternalException.runtime("Failed to generate IR from valid AST: " + err.toPrint());
        }
    }

    public Emitter(String unit, FnContext context) {
        this.unit = unit;
        this.context = new ABIContext(context);
    }

    /** 
     * The compilation unit. 
     */
    String unit;

    /**
     * The current loop
     */
    private IRLabel currentLoop;

    /**
     * Associated function name to ABI name context.
     */
    protected ABIContext context;

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
                    IRLabel andL = IRLabelFactory.generate("and");
                    return new IRSeq(
                        makeControlFlow(b.lhs, andL, falseL),
                        andL,
                        makeControlFlow(b.rhs, trueL, falseL)
                    );
                case OR:
                    IRLabel orL = IRLabelFactory.generate("and");
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

        // TODO: initialize globals

        // TODO: pass context to generate init function
        // - initialize global arrays
        // - initialize class size + vt
        program.appendFunc(Initializer.generateInit());

        for (Node n : p.body) {
            // Can ignore globals

            // TODO: visit classes


            if (n instanceof XiFn) {
                IRFuncDecl f = (IRFuncDecl) n.accept(this);
                program.appendFunc(f);
            }
        }

        return program;
    }

    @Override
    public IRNode visit(XiFn f) throws XicException {
        IRSeq body = (IRSeq) f.block.accept(this);

        // Bind arguments to temps
        List<IRNode> args = visit(f.args);
        for (int i = 0; i < args.size(); i++) {
            body.add(i, new IRMove((IRExpr) args.get(i), IRTempFactory.getArgument(i)));
        }

        // Insert empty return if needed
        if (body.size() == 0 || !(body.get(body.size() - 1) instanceof IRReturn)) {
            body.add(new IRReturn());
        }

        return new IRFuncDecl(f.id, context.lookup(f.id), body);
    }

    /*
     * Statement nodes
     */

    @Override
    public IRNode visit(XiAssign a) throws XicException {
        List<IRNode> lhs = visit(a.lhs);
        IRExpr rhs = (IRExpr) a.rhs.accept(this);

        if (lhs.size() == 1) {
            // If not an underscore
            IRExpr var = (IRExpr) lhs.get(0);
            if (var != null) {
                return new IRMove(var, rhs);
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
                stmts.add(new IRMove((IRExpr) n, IRTempFactory.getReturn(i)));
            }
        }

        return stmts;
    }

    // PA7
    @Override
    public IRNode visit(XiBreak b) {
        return new IRJump(new IRName(currentLoop));
    }

    @Override
    public IRNode visit(XiBlock b) throws XicException {
        IRSeq stmts = new IRSeq();
        for (Node n : b.statements) {
            IRNode stmt = n.accept(this);
            // For procedures
            if (stmt instanceof IRExpr) {
                stmts.add(new IRExp((IRExpr) stmt));
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

        IRTemp var = new IRTemp(d.id);
        if (!d.type.isPrimitive()) {

            // Case for array declaration with dimensions
            IRESeq arr = (IRESeq) d.xiType.accept(this);
            if (arr != null) {
                return new IRMove(var, arr);
            }
        }

        return var;
    }

    @Override
    public IRNode visit(XiIf i) throws XicException {
        IRSeq stmts = new IRSeq();
        IRLabel trueL = IRLabelFactory.generate("ifT");
        IRLabel falseL = IRLabelFactory.generate("ifF");

        stmts.add(makeControlFlow(i.guard, trueL, falseL));
        stmts.add(trueL);
        stmts.add((IRStmt) i.block.accept(this));
        stmts.add(falseL);
        if (i.hasElse()) {
            IRLabel doneL = IRLabelFactory.generate("ifDone");
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
        IRLabel headL = IRLabelFactory.generate("while");
        IRLabel trueL = IRLabelFactory.generate("whileT");
        IRLabel falseL = IRLabelFactory.generate("whileF");

        stmts.add(headL);
        stmts.add(makeControlFlow(w.guard, trueL, falseL));
        stmts.add(trueL);
        stmts.add((IRStmt) w.block.accept(this));
        stmts.add(Library.jump(headL));
        stmts.add(falseL);
        
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
                    return new IRCall(new IRName(Library.ARRAY_CONCAT), left, right);
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
                IRTemp andFlag = IRTempFactory.generate("and");
                IRLabel trueL = IRLabelFactory.generate("andT");
                IRLabel falseL = IRLabelFactory.generate("andF");
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
                IRTemp orFlag = IRTempFactory.generate("or");
                trueL = IRLabelFactory.generate("orT");
                falseL = IRLabelFactory.generate("orF");
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

        // TODO: PA7 update for method calls

        // Currently hacked for id instanceof XiVar
        IRName target = new IRName(context.lookup(((XiVar) c.id).id));
        List<IRExpr> argList = new ArrayList<>();
        for (Node n : c.getArgs()) {
            argList.add((IRExpr) n.accept(this));
        }
        return new IRCall(target, argList);
    }

    // PA7
    @Override
    public IRNode visit(XiDot d) {
        throw XicInternalException.runtime("Emit XiDot");
    }

    /**
     * Returns an expression that
     *  - containing the memory address for an array access on LHS.
     *  - the value at the memory address for an array access on RHS.
     */ 
    @Override
    public IRNode visit(XiIndex i) throws XicException {
        IRSeq stmts = new IRSeq();
        IRLabel doneL = IRLabelFactory.generate("done");

        // Store array reference copy if not already a temp
        IRExpr pointer = (IRExpr) i.array.accept(this);
        if (!(pointer instanceof IRTemp)) {
            IRTemp temp = IRTempFactory.generate("array");
            stmts.add(new IRMove(temp, pointer));
            pointer = temp;
        }

        // Store index if not already a temp or constant
        IRExpr index = (IRExpr) i.index.accept(this);
        if (!(index instanceof IRTemp || index instanceof IRConst)) {
            IRTemp temp = IRTempFactory.generate("index");
            stmts.add(new IRMove(temp, index));
            index = temp;
        }

        // Check bounds
        IRLabel outOfBounds = IRLabelFactory.generate("outOfBounds");
        stmts.add(new IRCJump(new IRBinOp(OpType.LT, index, Library.ZERO), outOfBounds));
        stmts.add(new IRCJump(new IRBinOp(OpType.GEQ, index, Library.length(pointer)), outOfBounds));
        stmts.add(Library.jump(doneL));
        stmts.add(outOfBounds);
        stmts.add(new IRExp(new IRCall(new IRName("_xi_out_of_bounds"))));
        stmts.add(doneL);

        IRExpr byteShift = new IRBinOp(OpType.MUL, Library.WORD_SIZE, index);
        IRExpr addr = new IRBinOp(OpType.ADD, pointer, byteShift);
        return new IRMem(new IRESeq(stmts, addr));
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
        return new IRTemp(v.id);
    }

    /*
     * Constant nodes
     */

    @Override
    public IRNode visit(XiArray a) throws XicException {
        return Library.alloc(visit(a.values));
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
        return Library.alloc(s);
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
            IRTemp size = IRTempFactory.generate("size");
            IRExpr sizeExpr =  (IRExpr) t.size.accept(this);
            IRESeq children = (IRESeq) t.child.accept(this);
            if (children == null) {
                IRSeq n = new IRSeq();
                n.add(new IRMove(size, sizeExpr));
                return new IRESeq(n, Library.alloc(size));
            } else {
                IRSeq sizes = (IRSeq) children.stmt();
                IRExpr alloc = (IRExpr) children.expr();
                sizes.add(0, new IRMove(size, sizeExpr));
                children.expr = Library.populate(size, alloc);
                return children;
            }
        } else {
            return null;
        }
    }

}   
