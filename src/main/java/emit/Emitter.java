package emit;

import java.util.ArrayList;

import ast.*;
import ir.*;
import type.FnContext;
import type.FnType;
import type.Type;
import xic.XicException;

public class Emitter extends Visitor<IRNode> {

    protected FnContext context;

    public Emitter(FnContext c) {
        context = c;
    }

    /* 
     * Utility methods
     */

    protected String makeABIName(String name) {
        FnType type = context.lookup(name);
        String args = type.args.toString();
        String returns = type.returns.toString();
        name = name.replaceAll("_", "__");
        return"_I" + name + returns + args;
    }


    /*
     * Top-level AST nodes
     */
    public IRNode visit(Program p) throws XicException {
        return null;
    }

    public IRNode visit(Use u) throws XicException {
        return null;
    }

    public IRNode visit(Fn f) throws XicException {
        return null;
    }

    /*
     * Statement nodes
     */
    public IRNode visit(Declare d) throws XicException {
        return null;
    }

    public IRNode visit(Assign a) throws XicException {
        return null;
    }

    public IRNode visit(Return r) throws XicException {
        return null;
    }

    public IRNode visit(Block b) throws XicException {
        ArrayList<IRNode> stmts = new ArrayList<>();
        for (Node n : b.statements) {
            stmts.add(n.accept(this));
        }
        return new IRSeq(stmts);
    }

    public IRNode visit(If i) throws XicException {
        return null;
    }

    public IRNode visit(While w) throws XicException {
        return null;
    }

    /*
     * Expression nodes
     */
    public IRNode visit(Call c) throws XicException {
        IRName id = new IRName(makeABIName(c.id));
        ArrayList<IRNode> argList = new ArrayList<>();
        for (Node n : c.getArgs()) {
            argList.add(n.accept(this));
        }
        return new IRCall(id, argList);
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

    public IRNode visit(Multiple m) throws XicException {
        return null;
    }

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

    public IRNode visit(XiString s) throws XicException {
        return null;
    }

    public IRNode visit(XiArray a) throws XicException {
        return null;
    }

}