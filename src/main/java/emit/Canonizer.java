package emit;

import ir.*;
import ir.IRBinOp.OpType;
import ir.IRMem.MemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Canonizer extends IRVisitor<IRNode> {
    
    /**
     * Returns a new IR AST that is the result of
     * canonizing the provided AST.
     *
     * The newly generated AST may contain references
     * to the original AST.
     */
    public static IRNode canonize(IRNode ast) {
        Canonizer canonizer = new Canonizer();
        return ast.accept(canonizer);
    }
    
    /**
     * Internal utility function for debugging.
     *
     * Returns the current list of statements for the
     * canonical version of the provided IR AST.
     */
    public static List<IRStmt> debug(IRNode ast) {
        Canonizer canonizer = new Canonizer();
        ast.accept(canonizer);
        return canonizer.stmts.stmts(); 
    }

    /**
     * The running list of statements in the current context.
     */
    private IRSeq stmts;

    /**
     * Constructor initializes @param stmts for debugging purposes.
     */
    private Canonizer() {
        stmts = new IRSeq();
    }
    
    /*
     * Visitor methods ---------------------------------------------------------------------
     */

    /**
     * Lowers an IRBinOp node by hoisting its expressions if necessary.
     * 
     * TODO: can be optimized by commuting
     */
    public IRNode visit(IRBinOp b) {
        IRExpr leftExpr = (IRExpr) b.left().accept(this);
        if (!leftExpr.isCanonical) {
            IRTemp temp = IRFactory.generate("binopL");
            stmts.add(new IRMove(temp, leftExpr));
            leftExpr = temp; 
        }

        IRExpr rightExpr = (IRExpr) b.right().accept(this);
        if (!rightExpr.isCanonical) {
            IRTemp temp = IRFactory.generate("binopR");
            stmts.add(new IRMove(temp, rightExpr));
            rightExpr = temp; 

        }

        // BinOp is does not need to be hoisted after hoisting its operands
        IRBinOp bop = new IRBinOp(b.type(), leftExpr, rightExpr);
        bop.isCanonical = true;
        return bop;
    }
    
    /**
     * Lowers an IRCall node by hoisting each argument if necessary and storing
     * the intermediate expression in a fresh temp before making the call.
     */
    public IRNode visit(IRCall c) {
        List<IRExpr> args = new ArrayList<>();
        
        for (IRExpr arg : c.args()) {
            IRExpr argExpr = (IRExpr) arg.accept(this);
            
            // Only hoist if necessary
            if (!argExpr.isCanonical) {
                IRTemp t = IRFactory.generate();
                stmts.add(new IRMove(t, argExpr));
                argExpr = t;
            }
            args.add(argExpr);
        }
        
        // Call is always hoisted
        return new IRCall(c.target(), c.numRets(), args);
    }

    /**
     * Lowers an IRCJump node by hoisting its expression if needed.
     * Requires: IRCJump only has a true label.
     */
    public IRNode visit(IRCJump c) {
        IRExpr condition = (IRExpr) c.cond.accept(this);

        // Hoist condition expression 
        if (!condition.isCanonical) {
            IRTemp t = IRFactory.generate("cjump");
            stmts.add(new IRMove(t, condition));
            condition = t;
        }

        stmts.add(new IRCJump(condition, c.trueLabel()));
        return null;
    }

    /**
     * Lowers an IRJump node by hoisting its expression if needed.
     */
    public IRNode visit(IRJump j) {
        if (j.hasLabel()) {
            stmts.add(j);
        } else {
            IRExpr targetExpr = (IRExpr) j.target().accept(this);

            // Hoist target expression if needed.
            if (!targetExpr.isCanonical) {
                IRTemp t = IRFactory.generate("cjump");
                stmts.add(new IRMove(t, targetExpr));
                targetExpr = t;
            }

            stmts.add(new IRJump(targetExpr));
        }
        return null;
    }
    
    /**
     * Lowers an IRCompUnit by lowering each function body.
     */
    public IRNode visit(IRCompUnit c) {
        Map<String, IRFuncDecl> lowered = new HashMap<>();
        
        // Globals do not require lowering

        // Lower each function
        for (IRFuncDecl fn : c.functions().values()) {
            lowered.put(fn.name(), (IRFuncDecl) fn.accept(this));
        }
        
        return new IRCompUnit(c.name(), c.globals(), lowered);
    }

    /**
     * Trivially lowers an IRConst node, which is an expression leaf.
     */
    public IRNode visit(IRConst c) {
        c.isCanonical = true;
        return c;
    }

    /**
     * Lowers an IRSeq node by evaluating its statement, and then
     * hoisting its expression and passing its result up.
     */
    public IRNode visit(IRESeq e) {
        e.stmt().accept(this); 
        return e.expr().accept(this);
    }

    /**
     * Lowers an IRExp by adding it to the list of current statements.
     */
    public IRNode visit(IRExp e) {
        IRExpr expr = (IRExpr) e.expr().accept(this);
        if (expr instanceof IRCall) {
            stmts.add(new IRExp(expr));
        }
        return null;
    }

    /**
     * Lowers an IRFuncDecl by lowering its body into a
     * single IRSeq.
     */
    public IRNode visit(IRFuncDecl f) {
        stmts = new IRSeq();
        f.body().accept(this);
        return new IRFuncDecl(f.sourceName(), f.name(), f.args(), f.rets(), stmts);
    }

    /**
     * Lowers an IRLabel by adding it to the list of current
     * statements.
     */
    public IRNode visit(IRLabel l) {
        stmts.add(l);
        return null;
    }

    /**
     * Lowers an IRMem node by hoisting its inner expression as needed.
     */
    public IRNode visit(IRMem m) {
        /* Immutable is set during translation for:
            - constant array generation
            - dynamic allocation function
            - array concatenation function
           Where there is a guarantee that there is no change of aliasing
        */

        if (m.memType() == MemType.IMMUTABLE) {
            m.isCanonical = true;
            return m;
        }

        IRExpr expr = (IRExpr) m.expr().accept(this);
        if (!expr.isCanonical) {
            IRTemp t = IRFactory.generate("mem");
            stmts.add(new IRMove(t, expr));
            expr = t;
        }

        // Mems are not canonical due to aliasing
        return new IRMem(expr);
    }

    /**
     * Lowers an IRMove node: if moving to a memory location,
     * hoist and evaluate the location, store result in temp, and then hoist
     * the source expression.
     * 
     * Otherwise location is a temp and cannot be affected by hoisting
     * the source expression first.
     * 
     * TODO: can be optimized by commuting
     */
    public IRNode visit(IRMove m) {
        
        // TODO: do special cases for globals
        // Preserve so at assembly
        // Mem(Temp(_G))        -> _G
        // Mem(Mem(Temp(_G)))   -> $_G

        // Need to check memory targets
        if (m.isMem()) {
            IRMem mem = m.getMem();

            // Hoist mem expression if not certain about immutability
            IRExpr dest = mem;
            if (mem.memType() != MemType.IMMUTABLE) {
                IRTemp temp = IRFactory.generate();
                IRExpr memExpr = (IRExpr) mem.expr().accept(this);
                stmts.add(new IRMove(temp, memExpr));
                dest = new IRMem(temp);
            }

            IRExpr srcExpr = (IRExpr) m.src().accept(this);
            stmts.add(new IRMove(dest, srcExpr));

        // Temporary targets don't need to be hoisted
        } else {
            IRExpr srcExpr = (IRExpr) m.src().accept(this);
            stmts.add(new IRMove(m.target(), srcExpr));
        }
        return null;
    }

    /**
     * Trivially lowers an IRName node, which is an expression leaf.
     */
    public IRNode visit(IRName n) {
        n.isCanonical = true;
        return n;
    }

    /**
     * Lowers an IRReturn node by hoisting each of its arguments if neccesary
     * and storing the intermediate result in a temporary before the return.
     */
    public IRNode visit(IRReturn r) {
        List<IRExpr> temps = new ArrayList<>();
        
        for (IRExpr ret : r.rets()) {
            IRExpr retExpr = (IRExpr) ret.accept(this);

            // Only hoist if necessary
            if (!retExpr.isCanonical) {
                IRTemp temp = IRFactory.generate();
                stmts.add(new IRMove(temp, retExpr));
                retExpr = temp;
            }

            temps.add(retExpr);
        }
        
        stmts.add(new IRReturn(temps));
        return null;
    }

    /**
     * Lowers an IRSeq node by flattening it into a list of statements.
     */
    public IRNode visit(IRSeq s) {
        for (IRNode stmt : s.stmts()) {
            stmt.accept(this);
        }
        return null;
    }

    /**
     * Lowers an IRTemp node, which is a global memory address or an expression leaf.
     */
    public IRNode visit(IRTemp t) {

        // Globals are hoisted
        if (t.global()) { return t; }

        // All other temporaries are lowered
        t.isCanonical = true;
        return t;
    }
}
