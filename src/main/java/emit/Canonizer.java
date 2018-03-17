package emit;

import ir.*;

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
    public static List<IRNode> debug(IRNode ast) {
		Canonizer canonizer = new Canonizer();
		ast.accept(canonizer);
        return canonizer.stmts; 
    }

    /**
     * The running list of statements in the current context.
     */
	private List<IRNode> stmts;

    /**
     * Constructor initializes @param stmts for debugging purposes.
     */
    private Canonizer() {
        stmts = new ArrayList<>();
    }

	/**
	 * Lowers an IRBinOp node by hoisting its first expression,
	 * moving the first expression to a temp, and then hoisting
	 * and evaluating the second expression.
	 * 
	 * TODO: can be optimized by checking for commuting.
	 */
	public IRNode visit(IRBinOp b) {
		IRTemp temp = IRTempFactory.generateTemp();
		IRNode leftExpr = b.left.accept(this);
		stmts.add(new IRMove(temp, leftExpr));
		IRNode rightExpr = b.right.accept(this);
		return new IRBinOp(b.type, temp, rightExpr);
	}
	
	/**
	 * Lowers an IRCall node by hoisting each argument, storing
	 * the intermediate expressions in temps, and then calling
	 * the function with all temps.
	 */
	public IRNode visit(IRCall c) {
		List<IRNode> temps = new ArrayList<>();
		
		for (IRNode arg : c.args) {
			IRNode argExpr = arg.accept(this);
			IRTemp temp = IRTempFactory.generateTemp();
			temps.add(temp);
			stmts.add(new IRMove(temp, argExpr));
		}
		
		IRTemp result = IRTempFactory.generateTemp();
		stmts.add(new IRMove(result, new IRCall(c.target, temps)));
		return result;
	}

	/**
	 * Lowers an IRCJump node by hoisting its expression.
	 */
	public IRNode visit(IRCJump c) {
		IRNode condExpr = c.cond.accept(this);
		stmts.add(new IRCJump(condExpr, c.trueLabel, c.falseLabel));
		return null;
	}

	/**
	 * Lowers an IRJump node by hoisting its expression.
	 */
	public IRNode visit(IRJump j) {
		IRNode targetExpr = j.target.accept(this);
		stmts.add(new IRJump(targetExpr));
		return null;
	}
	
	/**
	 * Lowers an IRCompUnit by lowering each function body.
	 */
	public IRNode visit(IRCompUnit c) {
		Map<String, IRFuncDecl> lowered = new HashMap<>();
		
		for (IRFuncDecl fn : c.functions.values()) {
			lowered.put(fn.name, (IRFuncDecl) fn.accept(this));
		}
		
		return new IRCompUnit(c.name, lowered);
	}

	/**
	 * Trivially lowers an IRConst node, which is an expression leaf.
	 */
	public IRNode visit(IRConst c) {
		return c;
	}

	/**
	 * Lowers an IRSeq node by evaluating its statement, and then
	 * hoisting its expression.
	 */
	public IRNode visit(IRESeq e) {
        e.stmt.accept(this); 
		return e.expr.accept(this);
	}

	/**
	 * Lowers an IRExp node by discarding its expression.
	 */
	public IRNode visit(IRExp e) {
		e.expr.accept(this);
		return null;
	}

    /**
     * Lowers an IRFuncDecl by lowering its body into a
     * single IRSeq.
     */
	public IRNode visit(IRFuncDecl f) {
		stmts = new ArrayList<>();
		f.body.accept(this);
		return new IRFuncDecl(f.name, new IRSeq(stmts));
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
	 * Lowers an IRMem node by hoisting its inner expression.
	 */
	public IRNode visit(IRMem m) {
		return new IRMem(m.expr.accept(this));
	}

	/**
	 * Lowers an IRMove node: if moving to a memory location,
	 * hoist and evaluate the location, store result in temp, and then hoist
	 * the source expression.
	 * 
	 * Otherwise location is a temp and cannot be affected by hoisting
	 * the source expression first.
	 * 
	 * TODO: can be optimized by checking for commuting.
	 */
	public IRNode visit(IRMove m) {
		if (m.isMem()) {
			IRTemp temp = IRTempFactory.generateTemp();
			IRMem mem = m.getMem();
			IRNode memExpr = mem.expr.accept(this);
			stmts.add(new IRMove(temp, memExpr));
			IRNode srcExpr = m.src.accept(this);
			stmts.add(new IRMove(new IRMem(temp), srcExpr));
		} else {
			IRNode srcExpr = m.src.accept(this);
			stmts.add(new IRMove(m.target, srcExpr));
		}
		return null;
	}

	/**
	 * Trivially lowers an IRName node, which is an expression leaf.
	 */
	public IRNode visit(IRName n) {
		return n;
	}

	/**
	 * Lowers an IRReturn node by hoisting each of its arguments,
	 * storing intermediate values in temps.
	 */
	public IRNode visit(IRReturn r) {
		List<IRNode> temps = new ArrayList<>();
		
		for (IRNode ret : r.rets) {
			IRNode retExpr = ret.accept(this);
			IRTemp temp = IRTempFactory.generateTemp();
			temps.add(temp);
			stmts.add(new IRMove(temp, retExpr));
		}
		
		stmts.add(new IRReturn(temps));
		return null;
	}

	/**
	 * Lowers an IRSeq node by flattening it into a list of statements.
	 */
	public IRNode visit(IRSeq s) {
		for (IRNode stmt : s.stmts) {
			stmt.accept(this);
		}
		return null;
	}

	/**
	 * Trivially lowers an IRTemp node, which is an expression leaf.
	 */
	public IRNode visit(IRTemp t) {
		return t;
    }
}
