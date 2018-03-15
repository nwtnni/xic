package emit;

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
        this.typeContext = context;
        this.context = new ABIContext(context);
    }

    /**
     * Associated function context.
     */
    protected FnContext typeContext;

    protected ABIContext context;

    /* 
     * Utility methods
     */


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

    // TODO: populate namespace with imports
    // just iterate through the context and just ignore use statements
    public IRNode visit(Use u) throws XicException {
        return null;
    }

    public IRNode visit(Fn f) throws XicException {

        // TODO: visit args and prepend MOVE into TEMP to body
        // see interpret.Configuration for useful constants
        // see interpret.Sample for examples of how to use them
        IRNode args = f.args.accept(this);

        IRSeq body = (IRSeq) f.block.accept(this);

        if (!(body.stmts.get(body.stmts.size() - 1) instanceof IRReturn)) {
            body.stmts.add(new IRReturn());
        }

        return new IRFuncDecl(context.lookup(f.id), body);
    }

    /*
     * Statement nodes
     */

    public IRNode visit(Declare d) throws XicException {
        if (d.type.isPrimative()) {
            return new IRTemp(d.id);
        }
        // TODO: dealing with array declarations
        throw XicInternalException.internal("todo");
    }

    // TODO: assignment, cases:
    // declr, var, multiple, arrays
    public IRNode visit(Assign a) throws XicException {
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

    // TODO: if control flow with short circuit
    public IRNode visit(If i) throws XicException {
        return null;
    }

    // TODO: while
    public IRNode visit(While w) throws XicException {
        return null;
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

        // TODO: deal with calling convention for returns (probably in assign)

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