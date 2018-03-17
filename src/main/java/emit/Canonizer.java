package emit;

import ir.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Canonizer extends IRVisitor<Void> {
	
	public static IRNode canonize(IRNode ast) {
		Canonizer canonizer = new Canonizer();
		ast.accept(canonizer);
		return new IRSeq(
			canonizer.stmts
				.stream()
				.map(stmt -> (IRNode) stmt)
				.collect(Collectors.toList())
		);
	}
	
	private IRExpr expr;
	private List<IRStmt> stmts;

	public Void visit(IRBinOp b) {
		return null;
	}
	
	public Void visit(IRCall c) {
		return null;
	}

	public Void visit(IRCJump c) {
		c.cond.accept(this);
		stmts.add(new IRCJump(expr, c.trueLabel, c.falseLabel));
		expr = null;
		return null;
	}

	public Void visit(IRJump j) {
		j.target.accept(this);
		stmts.add(new IRJump(expr));
		expr = null;
		return null;
	}
	
	public Void visit(IRCompUnit c) {
		return null;
	}

	/**
	 * Trivially lowers an IRConst node, which is an expression leaf.
	 */
	public Void visit(IRConst c) {
		expr = c;
		return null;
	}

	/**
	 * Lowers an IRSeq node by evaluating its statement, and then
	 * hoisting its expression.
	 */
	public Void visit(IRESeq e) {
		stmts.add((IRStmt) e.stmt);
		e.expr.accept(this);
		return null;
	}

	public Void visit(IRExp e) {
		return null;
	}

	public Void visit(IRFuncDecl f) {
		return null;
	}

	public Void visit(IRLabel l) {
		return null;
	}

	/**
	 * Lowers an IRMem node by hoisting its inner expression.
	 */
	public Void visit(IRMem m) {
		m.expr.accept(this);
		expr = new IRMem(expr);
		return null;
	}

	public Void visit(IRMove m) {
		return null;
	}

	/**
	 * Trivially lowers an IRName node, which is an expression leaf.
	 */
	public Void visit(IRName n) {
		expr = n;
		return null;
	}

	public Void visit(IRReturn r) {
		return null;
	}

	public Void visit(IRSeq s) {
		return null;
	}

	/**
	 * Trivially lowers an IRTemp node, which is an expression leaf.
	 */
	public Void visit(IRTemp t) {
		expr = t;
		return null;
    }
}