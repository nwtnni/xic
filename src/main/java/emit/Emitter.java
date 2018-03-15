package emit;

import java.util.List;
import java.util.ArrayList;

import ast.*;
import ir.*;
import type.FnContext;
import xic.XicException;
import xic.XicInternalException;

public class Emitter extends Visitor<IRNode> {

    /**
     * Factory method to generate IR from the given AST.
	 * @param ast AST to typecheck
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
     * Associated function context.
     */
    protected ABIContext context;

    private int labelIndex;

    /* 
     * Utility methods
     */

     /**
      * Generate a new unique label name.
      */
    private String generateLabel() {
        return "label_"+Integer.toString(++labelIndex);
    }

    /**
     * Generate a conditional jump in IR code.
     */
    private IRNode generateBranch(IRNode cond, IRNode t, IRNode f) {
        String trueLabel = "true_" + generateLabel();
        String falseLabel = "false_" + generateLabel();
        String done = "done_" + generateLabel();

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

    private IRNode generateBranch(IRNode cond, IRNode t) {
        return generateBranch(cond, t, null);
    }

    /**
     * Generate a loop in IR code.
     */
    private IRNode generateLoop(IRNode guard, IRNode block) {
        String headLabel = "while_" + generateLabel();
        String trueLabel = "true_" + generateLabel();
        String falseLabel = "false_" + generateLabel();

        IRSeq loop = new IRSeq(
            new IRLabel(headLabel),
            new IRCJump(guard, trueLabel, falseLabel),
            new IRLabel(trueLabel),
            new IRJump(new IRName(headLabel)),
            new IRLabel(falseLabel)
            );

        return loop;
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

        // TODO: visit args and prepend MOVE into TEMP to body
        // see interpret.Configuration for useful constants
        // see interpret.Sample for examples of how to use them
        // IRNode args = f.args.accept(this);

        IRSeq body = (IRSeq) f.block.accept(this);

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
        if (d.type.isPrimitive()) {
            return new IRTemp(d.id);
        }
        // TODO: dealing with array declarations
        throw XicInternalException.internal("todo");
    }

    // TODO: assignment, cases:
    // declr, var, multiple, arrays
    public IRNode visit(Assign a) throws XicException {
        // TODO: deal with calling convention for returns

        return null;
    }

    public IRNode visit(Return r) throws XicException {
        if (r.hasValue()) {
            IRNode n = r.value.accept(this);
            if (n instanceof IRNodeList) {
                return new IRReturn(((IRNodeList) n).nodes());
            }
            return new IRReturn(n);
        }
        return new IRReturn();
    }

    public IRNode visit(Block b) throws XicException {
        ArrayList<IRNode> stmts = new ArrayList<>();
        for (Node n : b.statements) {
            IRNode stmt = n.accept(this);
            // TODO: this is just a hack for wrapping an EXPR with an EXP
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
        IRNode cond = i.guard.accept(this);
        IRNode t = i.block.accept(this);
        if (i.hasElse()) {
            IRNode f = i.elseBlock.accept(this);
            return generateBranch(cond, t, f);
        }
        return generateBranch(cond, t);
    }

    public IRNode visit(While w) throws XicException {
        IRNode guard = w.guard.accept(this);
        IRNode block = w.block.accept(this);
        return generateLoop(guard, block);
    }

    /*
     * Expression nodes
     */

    public IRNode visit(Call c) throws XicException {
        IRName target = new IRName(context.lookup(c.id));
        ArrayList<IRNode> argList = new ArrayList<>();
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
                return new IRBinOp(IRBinOp.OpType.LEQ, left, right);
            case LE:
                return new IRBinOp(IRBinOp.OpType.LT, left, right);
            case GE:
                return new IRBinOp(IRBinOp.OpType.GEQ, left, right);
            case GT:
                return new IRBinOp(IRBinOp.OpType.GT, left, right);
            case EQ:
                return new IRBinOp(IRBinOp.OpType.EQ, left, right);
            case NE:
                return new IRBinOp(IRBinOp.OpType.NEQ, left, right);
            // TODO: fix boolean operators to use control flow
            case AND:
                return new IRBinOp(IRBinOp.OpType.AND, left, right);
            case OR:
                return new IRBinOp(IRBinOp.OpType.OR, left, right);
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

    // TODO: multiple types
    public IRNode visit(Multiple m) throws XicException {
        return null;
    }

    // TODO: array indexing
    public IRNode visit(Index i) throws XicException {
        return null;
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

    // TODO: strings and arrays - should we even have strings at this point?
    public IRNode visit(XiString s) throws XicException {
        return null;
    }

    public IRNode visit(XiArray a) throws XicException {
        return null;
    }

}