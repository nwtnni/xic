package emit;

import java.util.List;
import java.util.ArrayList;

import ast.*;
import type.FnContext;
import ir.*;
import ir.IRBinOp.OpType;
import interpret.Configuration;
import xic.XicException;

public class Emitter extends Visitor<IRNode> {

    /**
     * Factory method to generate IR from the given AST.
	 * @param ast AST to generate into IR
     * @param context function context corresponding to the AST
	 * @throws XicException if a semantic error was found
     */
    public static IRCompUnit emitIR(Program ast, FnContext context) throws XicException {
        return (IRCompUnit) ast.accept(new Emitter(context));
    }

    public Emitter(FnContext context) {
        this.context = new ABIContext(context);
        this.labelIndex = 0;
        this.tempIndex = 0;
    }

    /**
     * Associated function name to ABI name context.
     */
    protected ABIContext context;

    private long labelIndex;
    private long tempIndex;

    private static final IRConst WORD_SIZE = new IRConst(Configuration.WORD_SIZE);
    private static final IRConst ZERO = new IRConst(0);
    private static final IRConst ONE = new IRConst(1);

    /* 
     * Utility methods for generating code
     */

    /**
     * Generate a new unique label name with description.
     */
    private String generateLabel(String name) {
        return "__label_" + Long.toString(++labelIndex);
    }

    /**
     * Generate a new temporary name.
     */
    private IRTemp generateTemp() {
        return new IRTemp("__temp_" + Long.toString(++tempIndex));
    }

    /**
     * Generate a new temporary with a descriptive name.
     */
    private IRTemp generateTemp(String name) {
        return new IRTemp(name + "__temp_" + Long.toString(++tempIndex));
    }

    /**
     * Generate the temp for argument i.
     */
    private IRTemp getArgument(int i) {
        return new IRTemp(Configuration.ABSTRACT_ARG_PREFIX + i);
    }

    /**
     * Generate the time for return i.
     */
    private IRTemp getReturn(int i) {
        return new IRTemp(Configuration.ABSTRACT_RET_PREFIX + i);
    }

    /**
     * Generate a conditional jump in IR code.
     */
    private IRNode generateBranch(IRNode cond, IRNode t, IRNode f) {
        String trueLabel = generateLabel("true");
        String falseLabel = generateLabel("false");
        String done = generateLabel("done");

        IRSeq stmts = new IRSeq(
            new IRCJump(cond, trueLabel, falseLabel),
            new IRLabel(trueLabel),
            t,
            new IRJump(new IRName(done)),
            new IRLabel(falseLabel),
            f,
            new IRLabel(done)
        );
        return stmts;
    }

    /**
     * Generate a conditional jump with no false block.
     */
    private IRNode generateBranch(IRNode cond, IRNode t) {
        String trueLabel = generateLabel("true");
        String falseLabel = generateLabel("false");
        
        IRSeq stmts = new IRSeq(
            new IRCJump(cond, trueLabel, falseLabel),
            new IRLabel(trueLabel),
            t,
            new IRLabel(falseLabel)
        );
        return stmts;
    }

    /**
     * Generate a loop in IR code.
     */
    private IRNode generateLoop(IRNode guard, IRNode block) {
        String headLabel = generateLabel("while");
        String trueLabel = generateLabel("true");
        String falseLabel = generateLabel("false");

        IRSeq loop = new IRSeq(
            new IRLabel(headLabel),
            new IRCJump(guard, trueLabel, falseLabel),
            new IRLabel(trueLabel),
            block,
            new IRJump(new IRName(headLabel)),
            new IRLabel(falseLabel)
            );

        return loop;
    }

    /**
     * Is the value of a pointer by shift * WORD_SIZE bytes.
     */
    private IRExpr shiftAddr(IRExpr pointer, int shift) {
        IRConst byteShift = new IRConst(shift * Configuration.WORD_SIZE);
        IRExpr addr = new IRBinOp(IRBinOp.OpType.ADD, pointer, byteShift);

        // IRESeq newPointer = new IRESeq(
        //     new IRMove(pointer, addr), 
        //     pointer
        // );
        // return newPointer;
        return addr;
    }

    /**
     * Is the value of a pointer by the value of shift * WORD_SIZE bytes.
     */
    private IRExpr shiftAddr(IRExpr pointer, IRExpr shift) {
        IRExpr byteShift = new IRBinOp(OpType.MUL, shift, WORD_SIZE);
        IRExpr addr = new IRBinOp(IRBinOp.OpType.ADD, pointer, byteShift);
        return addr;
    }

    /**
     * Increments a temp.
     */
    private IRStmt incr(IRTemp i) {
        IRExpr plus = new IRBinOp(IRBinOp.OpType.ADD, i, ONE);
        return new IRMove(i, plus);
    }

    /**
     * Allocate memory for an array and copy the values into memory.
     */
    public IRExpr alloc(List<IRNode> array) throws XicException {
        ArrayList<IRNode> stmts = new ArrayList<>();
        
        int length = array.size();
        IRConst size = new IRConst((length + 1) * Configuration.WORD_SIZE);
        
        IRExpr addr =  new IRCall(new IRName("_xi_alloc"), size);
        IRTemp pointer = generateTemp("array");
        stmts.add(new IRMove(pointer, addr));

        //Store length of array
        stmts.add(new IRMove(new IRMem(pointer), new IRConst(length)));

        // Storing array into memory
        for(int i = 0; i < length; i++) {
            IRNode n = array.get(i);
            stmts.add(new IRMove(new IRMem(shiftAddr(pointer, i + 1)), n));
        }

        return new IRESeq(new IRSeq(stmts), shiftAddr(pointer, 1));
    }

    /**
     * Allocate memory for a string.
     */
    public IRExpr alloc(XiString s) throws XicException {
        List<IRNode> chars = new ArrayList<>();
        for (Long c : s.value) {
            chars.add(new IRConst(c));
        }
        return alloc(chars);
    }

    /**
     * Dynamically allocate memory for an an array of size length
     */
    private IRExpr alloc(IRExpr length) {
        ArrayList<IRNode> stmts = new ArrayList<>();
        
        // Calculate size of array
        IRExpr byteSize = new IRBinOp(
            OpType.MUL,
            new IRBinOp(OpType.ADD, length, ONE), 
            WORD_SIZE
        );
        IRTemp size = generateTemp("size");
        stmts.add(new IRMove(size, byteSize));

        // Allocate memory and save pointer
        IRExpr addr =  new IRCall(new IRName("_xi_alloc"), size);
        IRTemp pointer = generateTemp("array");
        stmts.add(new IRMove(pointer, addr));

        //Store length of array
        stmts.add(new IRMove(new IRMem(pointer), length));

        return new IRESeq(new IRSeq(stmts), shiftAddr(pointer, 1));
    }

    /**
     * Dynamically allocate memory for an array of size length and
     * populate each entry with a copy of child. 
     */
    private IRExpr populate(IRExpr length, IRExpr child) {
        IRExpr array = generateTemp("array");
        IRTemp i = generateTemp("incr");
        IRESeq populated = new IRESeq(
            new IRSeq(
                new IRMove(array, alloc(length)),
                new IRMove(i, ZERO),
                generateLoop(
                    new IRBinOp(OpType.LT, i, length), 
                    new IRSeq(
                        new IRMove(new IRMem(shiftAddr(array, i)), child),
                        incr(i)
                    )
                )
            ), 
            array
        );
        return populated;
    }

    /**
     * Generate code for the length built-in function.
     */
    private IRNode length(IRExpr pointer) {
        return new IRMem(shiftAddr(pointer, -1));
    }

    /*
     * Visitor methods
     */

    /**
     * Returns a list of IRNodes from visiting a list of AST nodes.
     */
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

    public IRNode visit(Program p) throws XicException {
        IRCompUnit program = new IRCompUnit("program");
        for (Node n : p.fns) {
            IRFuncDecl f = (IRFuncDecl) n.accept(this);
            program.appendFunc(f);
        }
        return program;
    }

    public IRNode visit(Use u) throws XicException {
        return null;
    }

    public IRNode visit(Fn f) throws XicException {
        IRSeq body = (IRSeq) f.block.accept(this);

        // Bind arguments to temps
        List<IRNode> args = visit(f.args);
        for (int i = 0; i < args.size(); i++) {
            body.stmts.add(i, new IRMove(args.get(i), getArgument(i)));
        }

        // Insert empty return if needed
        if (!(body.stmts.get(body.stmts.size() - 1) instanceof IRReturn)) {
            body.stmts.add(new IRReturn());
        }

        return new IRFuncDecl(context.lookup(f.id), body);
    }

    /*
     * Statement nodes
     */

    public IRNode visit(Declare d) throws XicException {
        if (d.isUnderscore()) {
            return null;
        }
        IRTemp var = new IRTemp(d.id);
        if (!d.type.isPrimitive()) {
            IRNode arr = d.xiType.accept(this);
            if (arr != null) {
                return new IRESeq(new IRMove(var, arr), var);
            }
        }
        return var;
    }

    public IRNode visit(Assign a) throws XicException {
        List<IRNode> lhs = visit(a.lhs);
        IRNode rhs = a.rhs.accept(this);

        if (lhs.size() == 1) {
            if (lhs.get(0) != null) {
                return new IRMove(lhs.get(0), rhs);
            } else {
                return new IRExp(rhs);
            }
        }

        List<IRNode> stmts = new ArrayList<>();
        if (lhs.get(0) == null) {
            stmts.add(new IRExp(rhs));
        } else {
            stmts.add(new IRMove(lhs.get(0), rhs));
        }
        for (int i = 1; i < lhs.size(); i++) {
            IRNode n = lhs.get(i);
            if (n != null) {
                stmts.add(new IRMove(n, getReturn(i)));
            }
        }

        return new IRSeq(stmts);
    }

    public IRNode visit(Return r) throws XicException {
        if (r.hasValues()) {
            List<IRNode> values = new ArrayList<>();
            for (Node n : r.values) {
                values.add(n.accept(this));
            }
            return new IRReturn(values);
        }
        return new IRReturn();
    }

    public IRNode visit(Block b) throws XicException {
        List<IRNode> stmts = new ArrayList<>();
        for (Node n : b.statements) {
            IRNode stmt = n.accept(this);
            // TODO: is this just a hack for wrapping an EXPR with an EXP?
            // we need to find a better way to wrap function/procedure calls
            if (stmt instanceof IRExpr) {
                stmts.add(new IRExp(stmt));
            } else {
                stmts.add(stmt);
            }
        }
        return new IRSeq(stmts);
    }

    public IRNode visit(If i) throws XicException {
        // TODO: fix quard to use control flow
        IRNode cond = i.guard.accept(this);
        IRNode t = i.block.accept(this);
        if (i.hasElse()) {
            IRNode f = i.elseBlock.accept(this);
            return generateBranch(cond, t, f);
        }
        return generateBranch(cond, t);
    }

    public IRNode visit(While w) throws XicException {
        // TODO: fix quard to use control flow
        IRNode guard = w.guard.accept(this);
        IRNode block = w.block.accept(this);
        return generateLoop(guard, block);
    }

    /*
     * Expression nodes
     */

    public IRNode visit(Call c) throws XicException {
        if (c.id.equals("length")) {
            return length((IRExpr) c.args.get(0).accept(this));
        }

        IRName target = new IRName(context.lookup(c.id));
        List<IRNode> argList = new ArrayList<>();
        for (Node n : c.getArgs()) {
            argList.add(n.accept(this));
        }
        return new IRCall(target, argList);
    }

    public IRNode visit(Binary b) throws XicException {
        IRNode left = b.lhs.accept(this);
        IRNode right = b.rhs.accept(this);
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
                IRTemp b1 = generateTemp();
                IRESeq and = new IRESeq(
                    new IRSeq(
                        new IRMove(b1, new IRConst(0)),
                        generateBranch(left, new IRMove(b1, right))
                    ), 
                    b1
                );
                return and;
            case OR:
                IRExpr cond = new IRBinOp(IRBinOp.OpType.XOR, left, new IRConst(1));
                IRTemp b2 = generateTemp();
                IRESeq or = new IRESeq(
                    new IRSeq(
                        new IRMove(b2, new IRConst(1)),
                        generateBranch(cond, new IRMove(b2, right))
                    ), 
                    b2
                );
                return or;
        }
        // Unreachable
        assert false;
        return null;
    }

    public IRNode visit(Unary u) throws XicException {
        IRNode child = u.child.accept(this);
        if (u.isLogical()) {
            return new IRBinOp(IRBinOp.OpType.XOR, new IRConst(1), child);
        } else {
            return new IRBinOp(IRBinOp.OpType.SUB, new IRConst(0), child);
        }
    }

    public IRNode visit(Var v) throws XicException {
        return new IRTemp(v.id);
    }

    public IRNode visit(Index i) throws XicException {
        // TODO: check out of bounds
        IRExpr pointer = (IRExpr) i.array.accept(this);
        IRExpr index = (IRExpr) i.index.accept(this);
        IRExpr addr = shiftAddr(pointer, index);
        return new IRMem(addr);
    }

    public IRNode visit(XiInt i) throws XicException {
        return new IRConst(i.value);
    }

    public IRNode visit(XiBool b) throws XicException {
        long value = b.value ? 1 : 0;
        return new IRConst(value);
    }

    public IRNode visit(XiChar c) throws XicException {
        return new IRConst(c.value);
    }

    public IRNode visit(XiString s) throws XicException {
        return alloc(s);
    }

    public IRNode visit(XiArray a) throws XicException {
        return alloc(visit(a.values));
    }

    /*
     * Other nodes
     */

    public IRNode visit(XiType t) throws XicException {
        // Only allocate memory for special case of syntactic sugar
        // for array declarations with dimensions specified
        if (t.hasSize()) {
            IRExpr length = (IRExpr) t.size.accept(this);
            IRExpr child = (IRExpr) t.child.accept(this);
            if (child == null) {
                return alloc(length);
            } else {
                return populate(length, child);
            }
        } else {
            return null;
        }
    }
}