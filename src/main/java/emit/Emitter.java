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
    }

    /**
     * Associated function name to ABI name context.
     */
    protected ABIContext context;

    private long labelIndex;

    private static final IRConst WORD_SIZE = new IRConst(Configuration.WORD_SIZE);
    private static final IRConst ZERO = new IRConst(0);
    private static final IRConst ONE = new IRConst(1);

    /* 
     * Utility methods for generating code
     */

    /**
     * Generate a new unique label name with description.
     */
    private IRLabel generateLabel(String name) {
        return new IRLabel(name + "__label_" + Long.toString(++labelIndex));
    }

    // /**
    //  * Generate a conditional jump in IR code.
    //  */
    // private IRNode generateBranch(IRNode cond, IRNode t, IRNode f) {
    //     String trueLabel = generateLabel("true");
    //     String falseLabel = generateLabel("false");
    //     String done = generateLabel("done");

    //     IRSeq stmts = new IRSeq(
    //         new IRCJump(cond, trueLabel, falseLabel),
    //         new IRLabel(trueLabel),
    //         t,
    //         new IRJump(new IRName(done)),
    //         new IRLabel(falseLabel),
    //         f,
    //         new IRLabel(done)
    //     );
    //     return stmts;
    // }

    /**
     * Generate a conditional jump where true falls through.
     */
    private IRNode generateBranch(IRNode cond, IRNode t) {
        IRLabel tL = generateLabel("true");
        IRLabel fL = generateLabel("false");
        
        return new IRSeq(
            new IRCJump(cond, tL.name),
            new IRJump(new IRName(fL.name)),
            tL,
            t,
            fL
        );
    }

    /**
     * Generate a loop in IR code.
     */
    private IRNode generateLoop(IRNode guard, IRNode block) {
        IRLabel hL = generateLabel("while");
        IRLabel tL = generateLabel("true");
        IRLabel fL = generateLabel("false");

        return new IRSeq(
            hL,
            new IRCJump(guard, tL.name),
            new IRJump(new IRName(fL.name)),
            tL,
            block,
            new IRJump(new IRName(hL.name)),
            fL
        );
    }

    /**
     * Is the value of a expression shifted by shift * WORD_SIZE bytes.
     */
    private IRExpr shiftAddr(IRExpr pointer, IRExpr shift) {
        IRExpr byteShift = new IRBinOp(OpType.MUL, shift, WORD_SIZE);
        IRExpr addr = new IRBinOp(OpType.ADD, pointer, byteShift);
        return addr;
    }

    /**
     * Increment pointer by WORD_SIZE bytes.
     */
    private IRStmt incrPointer(IRTemp pointer) {
        IRExpr addr = new IRBinOp(OpType.ADD, pointer, WORD_SIZE);
        return new IRMove(pointer, addr);
    }

    /**
     * Increments a temp by 1.
     */
    private IRStmt increment(IRTemp i) {
        IRExpr plus = new IRBinOp(IRBinOp.OpType.ADD, i, ONE);
        return new IRMove(i, plus);
    }

    /**
     * Allocate memory for an array and copy the values into memory.
     */
    public IRExpr alloc(List<IRNode> array) throws XicException {
        ArrayList<IRNode> stmts = new ArrayList<>();
        
        // Calcuate size of array
        int length = array.size();
        IRConst size = new IRConst((length + 1) * Configuration.WORD_SIZE);
        
        // Generate pointers and allocate memory
        IRExpr addr =  new IRCall(new IRName("_xi_alloc"), size);
        IRTemp pointer = IRTempFactory.generateTemp("array");
        IRTemp workPointer = IRTempFactory.generateTemp("work_ptr");
        stmts.add(new IRMove(pointer, addr));
        stmts.add(new IRMove(workPointer, pointer));
        // Shift pointer to head of array
        stmts.add(incrPointer(pointer));

        //Store length of array
        stmts.add(new IRMove(new IRMem(workPointer), new IRConst(length)));

        // Storing values of array into memory
        for(int i = 0; i < length; i++) {
            IRNode n = array.get(i);
            stmts.add(incrPointer(workPointer));
            stmts.add(new IRMove(new IRMem(workPointer), n));
        }

        return new IRESeq(
            new IRSeq(stmts), 
            pointer,
            array
        );
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
        List<IRNode> stmts = new ArrayList<>();

        // Calculate size of array
        IRExpr byteSize = new IRBinOp(
            OpType.MUL,
            new IRBinOp(OpType.ADD, length, ONE), 
            WORD_SIZE
        );

        IRTemp size = IRTempFactory.generateTemp("d_size");
        stmts.add(new IRMove(size, byteSize));

        // Generate pointers and llocate memory
        IRExpr addr =  new IRCall(new IRName("_xi_alloc"), size);
        IRTemp pointer = IRTempFactory.generateTemp("d_array");
        stmts.add(new IRMove(pointer, addr));

        // Store length then shift pointer
        stmts.add(new IRMove(new IRMem(pointer), length));
        stmts.add(incrPointer(pointer));

        return new IRESeq(
            new IRSeq(stmts),
            pointer
        );
    }

    /**
     * Dynamically allocate memory for an array of length size and
     * populate each entry with a copy of child. 
     */
    private IRExpr populate(IRExpr size, IRExpr child) {
        List<IRNode> stmts = new ArrayList<>();

        // Generate pointers and allocate memory
        IRTemp pointer = IRTempFactory.generateTemp("populate_array");
        IRTemp workPointer = IRTempFactory.generateTemp("work_ptr");
        stmts.add(new IRMove(pointer, alloc(size)));
        stmts.add(new IRMove(workPointer, pointer));

        // Create copies of the child (so no checking if child is an alloc)
        IRTemp i = IRTempFactory.generateTemp("i");
        stmts.add(new IRMove(i, ZERO));
        stmts.add(generateLoop(
            new IRBinOp(OpType.LT, i, size),
            new IRSeq(
                new IRMove(new IRMem(workPointer), child),
                incrPointer(workPointer),
                increment(i)
            )
        ));

        return new IRESeq(
            new IRSeq(stmts), 
            pointer
        );
    }

    /**
     * Generate code for the length built-in function.
     */
    private IRExpr length(IRExpr pointer) {
        return new IRMem(new IRBinOp(OpType.SUB, pointer, WORD_SIZE));
    }

    /**
     * Generate code for concatenating two arrays.
     */
    protected IRExpr concat(IRExpr a, IRExpr b) {
        List<IRNode> stmts = new ArrayList<>();

        // Make copies of pointers
        IRTemp ap = IRTempFactory.generateTemp("a_ptr_copy");
        stmts.add(new IRMove(ap, a));
        IRTemp bp = IRTempFactory.generateTemp("b_ptr_copy");
        stmts.add(new IRMove(bp, b));

        // Calculate new array size
        IRExpr aLen = IRTempFactory.generateTemp("a_len");
        stmts.add(new IRMove(aLen, length(ap)));
        IRExpr bLen = IRTempFactory.generateTemp("b_len");
        stmts.add(new IRMove(bLen, length(bp)));
        IRTemp size = IRTempFactory.generateTemp("concat_size");
        stmts.add(new IRMove(size, new IRBinOp(OpType.ADD, aLen, bLen)));

        // Generate pointers and allocate memory
        IRTemp pointer = IRTempFactory.generateTemp("concat_array");
        IRTemp workPointer = IRTempFactory.generateTemp("work_ptr");
        stmts.add(new IRMove(pointer, alloc(size)));
        stmts.add(new IRMove(workPointer, pointer));

        IRTemp i = IRTempFactory.generateTemp("i");
        stmts.add(new IRMove(i, ZERO));
        stmts.add(generateLoop(
            new IRBinOp(OpType.LT, i, aLen), 
            new IRSeq(
                new IRMove(new IRMem(workPointer), new IRMem(ap)),
                increment(i),
                incrPointer(workPointer),
                incrPointer(ap)
            )
        ));
        stmts.add(new IRMove(i, ZERO));
        stmts.add(generateLoop(
            new IRBinOp(OpType.LT, i, bLen), 
            new IRSeq(
                new IRMove(new IRMem(workPointer), new IRMem(bp)),
                increment(i),
                incrPointer(workPointer),
                incrPointer(bp)
            )
        ));

        return new IRESeq(
            new IRSeq(stmts),
            pointer
        );
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
            body.stmts.add(i, new IRMove(args.get(i), IRTempFactory.getArgument(i)));
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
                return new IRMove(var, arr);
            }
        }
        return var;
    }

    public IRNode visit(Assign a) throws XicException {
        List<IRNode> lhs = visit(a.lhs);
        IRNode rhs = a.rhs.accept(this);

        if (lhs.size() == 1) {
            // If not an underscore
            IRExpr var = (IRExpr) lhs.get(0);
            if (var != null) {
                // Wrap array access with mem
                if (var instanceof IRESeq) {
                    var = new IRMem(var);
                }
                return new IRMove(var, rhs);
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
                stmts.add(new IRMove(n, IRTempFactory.getReturn(i)));
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
            // For procedures
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
            IRLabel done = generateLabel("done");
            return new IRSeq(
                generateBranch(cond, 
                    new IRSeq(t, new IRJump(new IRName(done.name)))
                ),
                f,
                done
            );
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
                    return concat(left, right);
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
                IRTemp b1 = IRTempFactory.generateTemp();
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
                IRTemp b2 = IRTempFactory.generateTemp();
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

    /**
     * Returns an expression that
     *  - containing the memory address for an array access on LHS.
     *  - the value at the memory address for an array access on RHS.
     */ 
    public IRNode visit(Index i) throws XicException {
        List<IRNode> stmts = new ArrayList<>();
        IRLabel done = generateLabel("done");
        IRExpr result = IRTempFactory.generateTemp("result");

        // Store index
        IRTemp index = IRTempFactory.generateTemp("index");
        stmts.add(new IRMove(index, i.index.accept(this)));

        // Store array reference
        IRTemp pointer = IRTempFactory.generateTemp("array_ref");
        stmts.add(new IRMove(pointer, i.array.accept(this)));


        // Check bounds
        stmts.add(
            generateBranch(
                new IRBinOp(OpType.GEQ, index, ZERO), 
                generateBranch(
                    new IRBinOp(OpType.LT, index, length(pointer)),
                    new IRSeq(
                        new IRMove(result, shiftAddr(pointer, index)),
                        new IRJump(new IRName(done.name))
                    )
                )
            )
        );
        stmts.add(new IRExp(new IRCall(new IRName("_xi_out_of_bounds"))));
        stmts.add(done);


        // Different cases for array on LHS and RHS
        if (i.isExpr || i.array instanceof Index) {
            result = new IRMem(result);
        }

        return new IRESeq(new IRSeq(stmts), result);
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