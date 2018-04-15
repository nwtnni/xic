package emit;

import java.util.List;
import java.util.ArrayList;

import ast.*;
import type.FnContext;
import ir.*;
import ir.IRBinOp.OpType;
import ir.IRMem.MemType;
import interpret.Configuration;
import xic.XicException;
import util.Pair;

/**
 * Main decorated AST to IR translation implementation. Recursively 
 * traverses the AST and constructs a new IR tree that represents the AST.
 */
public class Emitter extends Visitor<IRNode> {

    /**
     * Factory method to generate IR from the given AST.
     * @param ast AST to generate into IR
     * @param context function context corresponding to the AST
     * @throws XicException if a semantic error was found
     */
    public static Pair<IRCompUnit, ABIContext> emitIR(Program ast, FnContext context) throws XicException {
        IRTempFactory.reset();
        Emitter e = new Emitter(context);
        return new Pair<>((IRCompUnit) ast.accept(e), e.context);
    }

    public Emitter(FnContext context) {
        this.context = new ABIContext(context);
    }

    /**
     * Associated function name to ABI name context.
     */
    protected ABIContext context;

    protected static final IRConst WORD_SIZE = new IRConst(Configuration.WORD_SIZE);
    protected static final IRConst ZERO = new IRConst(0);
    protected static final IRConst ONE = new IRConst(1);

    // TODO: move shared configuration into config classes
    // ABI names for array library functions ignore the types of arrays
    // and treat each argument as a 64-bit pointer (equivalent to an integer)
    protected static final String ARRAY_ALLOC = "_xi_d_alloc";
    protected static final String ARRAY_CONCAT = "_xi_array_concat";

    /* 
     * Utility methods for code generation
     */

    /**
     * Make a jump to a label.
     */
    private IRJump jump(IRLabel l) {
        return new IRJump(new IRName(l.name));
    }

    /**
     * Generate a conditional jump using C translations.
     */
    private IRNode makeControlFlow(Node n, IRLabel trueL, IRLabel falseL) throws XicException {
        if (n instanceof XiBool) {
            XiBool b = (XiBool) n;
            if (b.value) {
                return jump(trueL);
            } else {
                return jump(falseL);
            }
        } else if (n instanceof Binary) {
            Binary b = (Binary) n;
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
        } else if (n instanceof Unary) {
            Unary u = (Unary) n;
            if (u.isLogical()) {
                makeControlFlow(u.child, falseL, trueL);
            }
        }
        return new IRSeq(
            new IRCJump(n.accept(this), trueL.name),
            jump(falseL)
        );
    }

    /**
     * Generate a loop in IR code given a IR node guard and body.
     */
    private IRNode generateLoop(String name, IRNode guard, IRNode block) {
        IRLabel headL = IRLabelFactory.generate(name);
        IRLabel trueL = IRLabelFactory.generate("true");
        IRLabel falseL = IRLabelFactory.generate("false");

        return new IRSeq(
            headL,
            new IRCJump(guard, trueL.name),
            jump(falseL),
            trueL,
            block,
            jump(headL),
            falseL
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
        IRTemp pointer = IRTempFactory.generate("array");
        stmts.add(new IRMove(pointer, addr));

        //Store length of array
        stmts.add(new IRMove(new IRMem(pointer), new IRConst(length)));

        // Storing values of array into memory
        for(int i = 0; i < length; i++) {
            IRNode n = array.get(i);

            // index = j(workpointer)
            IRConst j = new IRConst((i + 1) * WORD_SIZE.value); 
            IRExpr index = new IRBinOp(OpType.ADD, pointer, j);
            IRMem elem = new IRMem(index, MemType.IMMUTABLE);
            
            stmts.add(new IRMove(elem, n));
        }

        // Shift pointer to head of array
        stmts.add(incrPointer(pointer));

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
        return new IRCall(new IRName(ARRAY_ALLOC), length);
    }

    /**
     * Dynamically allocate memory for an array of length size and
     * populate each entry with a copy of child. 
     */
    private IRExpr populate(IRExpr size, IRExpr child) {
        List<IRNode> stmts = new ArrayList<>();

        // Generate pointers and allocate memory
        IRTemp pointer = IRTempFactory.generate("populate_array");
        stmts.add(new IRMove(pointer, alloc(size)));

        // Create copies of the child (so no checking if child is an alloc)
        // addr = (workPointer,i,8)
        IRTemp i = IRTempFactory.generate("i");
        IRExpr index = new IRBinOp(OpType.MUL, i, new IRConst(8));
        IRMem addr = new IRMem(new IRBinOp(OpType.ADD, pointer, index), MemType.IMMUTABLE);
        
        stmts.add(new IRMove(i, ZERO));
        stmts.add(generateLoop(
            "make_array_loop",
            new IRBinOp(OpType.LT, i, size),
            new IRSeq(
                new IRMove(addr, child),
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

    /*
     * Library functions
     */

    /**
     * Generates library function for allocating memory for an dynamic array.
     */
    private IRFuncDecl xiDynamicAlloc() {
        List<IRNode> stmts = new ArrayList<>();

        IRTemp length = IRTempFactory.generate("d_length");
        stmts.add(new IRMove(length, IRTempFactory.getArgument(0)));

        // Calculate size of array
        IRExpr byteSize = new IRBinOp(
            OpType.MUL,
            new IRBinOp(OpType.ADD, length, ONE), 
            WORD_SIZE
        );

        IRTemp size = IRTempFactory.generate("d_size");
        stmts.add(new IRMove(size, byteSize));

        // Generate pointers and llocate memory
        IRExpr addr =  new IRCall(new IRName("_xi_alloc"), size);
        IRTemp pointer = IRTempFactory.generate("d_array");
        stmts.add(new IRMove(pointer, addr));

        // Store length then shift pointer
        stmts.add(new IRMove(new IRMem(pointer), length));
        stmts.add(incrPointer(pointer));

        stmts.add(new IRReturn(pointer));

        return new IRFuncDecl(ARRAY_ALLOC, new IRSeq(stmts));
    }

    /**
     * Generates library function for accessing an array.
     * _xi_array_concat(a, b)
     */
    private IRFuncDecl xiArrayConcat() {
        List<IRNode> body = new ArrayList<>();

        // Make copies of pointers
        IRTemp ap = IRTempFactory.generate("a_ptr_copy");
        body.add(new IRMove(ap, IRTempFactory.getArgument(0)));
        IRTemp bp = IRTempFactory.generate("b_ptr_copy");
        body.add(new IRMove(bp, IRTempFactory.getArgument(1)));

        // Calculate new array size
        IRExpr aLen = IRTempFactory.generate("a_len");
        body.add(new IRMove(aLen, length(ap)));
        IRExpr bLen = IRTempFactory.generate("b_len");
        body.add(new IRMove(bLen, length(bp)));
        IRTemp size = IRTempFactory.generate("concat_size");
        body.add(new IRMove(size, new IRBinOp(OpType.ADD, aLen, bLen)));

        // Generate pointers and allocate memory
        IRTemp pointer = IRTempFactory.generate("concat_array");
        body.add(new IRMove(pointer, alloc(size)));

        IRTemp i = IRTempFactory.generate("i");
        IRExpr index = new IRBinOp(OpType.MUL, i, new IRConst(8));
        IRMem addr = new IRMem(new IRBinOp(OpType.ADD, pointer, index), MemType.IMMUTABLE);
        IRMem aElem = new IRMem(new IRBinOp(OpType.ADD, ap, index), MemType.IMMUTABLE);
        
        body.add(new IRMove(i, ZERO));
        body.add(generateLoop(
            "copy_a_loop",
            new IRBinOp(OpType.LT, i, aLen), 
            new IRSeq(
                new IRMove(addr, aElem),
                increment(i)
            )
        ));

        IRTemp j = IRTempFactory.generate("j");
        IRExpr indexb = new IRBinOp(OpType.MUL, j, new IRConst(8));
        IRMem bElem = new IRMem(new IRBinOp(OpType.ADD, bp, indexb), MemType.IMMUTABLE);

        body.add(new IRMove(j, ZERO));
        body.add(generateLoop(
            "copy_b_loop",
            new IRBinOp(OpType.LT, j, bLen), 
            new IRSeq(
                new IRMove(addr, bElem),
                increment(i),
                increment(j)
            )
        ));

        body.add(new IRReturn(pointer));

        return new IRFuncDecl(ARRAY_CONCAT, new IRSeq(body));
    }

    /*
     * Visitor methods ---------------------------------------------------------------------
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

        program.appendFunc(xiArrayConcat());
        program.appendFunc(xiDynamicAlloc());

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
        if (body.stmts.size() == 0 || !(body.stmts.get(body.stmts.size() - 1) instanceof IRReturn)) {
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

            // Case for array declaration with dimensions
            IRESeq arr = (IRESeq) d.xiType.accept(this);
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
        List<IRNode> nodes = new ArrayList<>();
        IRLabel trueL = IRLabelFactory.generate("true");
        IRLabel falseL = IRLabelFactory.generate("false");

        nodes.add(makeControlFlow(i.guard, trueL, falseL));
        nodes.add(trueL);
        nodes.add(i.block.accept(this));
        nodes.add(falseL);
        if (i.hasElse()) {
            IRLabel doneL = IRLabelFactory.generate("done");
            nodes.add(nodes.size() - 1, jump(doneL));
            nodes.add(i.elseBlock.accept(this));
            nodes.add(doneL);
        }
        return new IRSeq(nodes);
    }

    public IRNode visit(While w) throws XicException {
        List<IRNode> nodes = new ArrayList<>();
        IRLabel headL = IRLabelFactory.generate("while");
        IRLabel trueL = IRLabelFactory.generate("true");
        IRLabel falseL = IRLabelFactory.generate("false");

        nodes.add(headL);
        nodes.add(makeControlFlow(w.guard, trueL, falseL));
        nodes.add(trueL);
        nodes.add(w.block.accept(this));
        nodes.add(jump(headL));
        nodes.add(falseL);
        
        return new IRSeq(nodes);

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
                    return new IRCall(new IRName(ARRAY_CONCAT), left, right);
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
                IRLabel trueL = IRLabelFactory.generate("true");
                IRLabel falseL = IRLabelFactory.generate("false");
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
                trueL = IRLabelFactory.generate("true");
                falseL = IRLabelFactory.generate("false");
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
        IRLabel doneL = IRLabelFactory.generate("done");
        IRExpr result = IRTempFactory.generate("result");

        // Store array reference
        IRTemp pointer = IRTempFactory.generate("array_ref");
        stmts.add(new IRMove(pointer, i.array.accept(this)));

        // Store index
        IRTemp index = IRTempFactory.generate("index");
        stmts.add(new IRMove(index, i.index.accept(this)));

        // Check bounds
        IRLabel outOfBounds = IRLabelFactory.generate("out_of_bounds");
        stmts.add(new IRCJump(new IRBinOp(OpType.LT, index, ZERO), outOfBounds.name));
        stmts.add(new IRCJump(new IRBinOp(OpType.GEQ, index, length(pointer)), outOfBounds.name));
        stmts.add(new IRMove(result, shiftAddr(pointer, index)));
        stmts.add(jump(doneL));
        stmts.add(outOfBounds);
        stmts.add(new IRExp(new IRCall(new IRName("_xi_out_of_bounds"))));
        stmts.add(doneL);

        return new IRMem(new IRESeq(new IRSeq(stmts), result));
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
            IRTemp size = IRTempFactory.generate("size");
            IRExpr sizeExpr =  (IRExpr) t.size.accept(this);
            IRESeq children = (IRESeq) t.child.accept(this);
            if (children == null) {
                List<IRNode> n = new ArrayList<>();
                n.add(new IRMove(size, sizeExpr));
                IRESeq tuple = new IRESeq(
                    new IRSeq(n), 
                    alloc(size));
                return tuple;
            } else {
                IRSeq sizes = (IRSeq) children.stmt;
                IRExpr alloc = (IRExpr) children.expr;
                sizes.stmts.add(0, new IRMove(size, sizeExpr));
                children.expr = populate(size, alloc);
                return children;
            }
        } else {
            return null;
        }
    }

}   
