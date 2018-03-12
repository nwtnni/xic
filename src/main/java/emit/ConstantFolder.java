package emit;

import java.util.OptionalLong;
import ir.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConstantFolder extends IRVisitor<OptionalLong> {

    IRNode tree;

	public OptionalLong visit(IRBinOp b) {
		OptionalLong lt = b.left.accept(this);
        OptionalLong rt = b.right.accept(this);
        if (lt.isPresent() && rt.isPresent()) {
            long c;
            switch (b.type) {
            case ADD: 
                c = lt.getAsLong() + rt.getAsLong();
                break;
            case SUB:
                c = lt.getAsLong() - rt.getAsLong();
                break;
            case MUL:
                c = lt.getAsLong() * rt.getAsLong();
                break;
            case HMUL:
                c = BigInteger.valueOf(lt.getAsLong()).
                    multiply(BigInteger.valueOf(lt.getAsLong())).shiftRight(64).longValue();
                break;
            case DIV:
                c = lt.getAsLong() / rt.getAsLong();
                break;
            case MOD:
                c = lt.getAsLong() % rt.getAsLong();
                break;
            case AND:
                c = ((lt.getAsLong() == 1) && (rt.getAsLong() == 1)) ? 1 : 0;
                break;
            case OR:
                c = ((lt.getAsLong() == 1) || (rt.getAsLong() == 1)) ? 1 : 0;
                break;
            case XOR:
                c = lt.getAsLong() ^ rt.getAsLong();
                break;
            case LSHIFT:
                c = lt.getAsLong() << rt.getAsLong();
                break;
            case RSHIFT:
                c = lt.getAsLong() >> rt.getAsLong();
                break;
            case ARSHIFT:
                c = lt.getAsLong() >>> rt.getAsLong();
                break;
            case EQ:
                c = ((lt.getAsLong() == 1) == (rt.getAsLong() == 1)) ? 1 : 0;
                break;
            case NEQ:
                c = ((lt.getAsLong() == 1) != (rt.getAsLong() == 1)) ? 1 : 0;
                break;
            case LT:
                c = (lt.getAsLong() < rt.getAsLong()) ? 1 : 0;
                break;
            case GT:
                c = (lt.getAsLong() > rt.getAsLong()) ? 1 : 0;
                break;
            case LEQ:
                c = (lt.getAsLong() <= rt.getAsLong()) ? 1 : 0;
                break;
            case GEQ:
                c = (lt.getAsLong() >= rt.getAsLong()) ? 1 : 0;
                break;
            default: // unreachable
                assert false;
                return null;
            }
            return OptionalLong.of(c);
        }
        return OptionalLong.empty();
        
	}
	
	public OptionalLong visit(IRCall c) {
        List<IRNode> children = new ArrayList<IRNode>();
		for (IRNode n : c.args) {
            OptionalLong ol = n.accept(this);
            if (ol.isPresent()) {
                children.add(new IRConst(ol.getAsLong()));
            } else {
                children.add(n);
            }
        }
        c.args = children;
        return OptionalLong.empty();
	}

	public OptionalLong visit(IRCJump c) {
        OptionalLong ol = c.accept(this);
        if (ol.isPresent()) {
            c.cond = new IRConst(ol.getAsLong());
        }
		return OptionalLong.empty();
	}

	public OptionalLong visit(IRJump j) {
		return OptionalLong.empty();
	}
	
	public OptionalLong visit(IRCompUnit c) {
		return OptionalLong.empty();
	}

	public OptionalLong visit(IRConst c) {
		return OptionalLong.of(c.value);
	}

	public OptionalLong visit(IRESeq e) {
        /* e.stmt cannot be constant, do not need to check */
		OptionalLong sol = e.stmt.accept(this);
        OptionalLong eol = e.expr.accept(this);

        if (eol.isPresent()) {
            e.expr = new IRConst(eol.getAsLong());
        }

        return OptionalLong.empty();
	}

	public OptionalLong visit(IRExp e) {
		OptionalLong eol = e.expr.accept(this);

        if (eol.isPresent()) {
            return OptionalLong.of(eol.getAsLong());
        }

        return OptionalLong.empty();
	}

	public OptionalLong visit(IRFuncDecl f) {
        OptionalLong bol = f.body.accept(this);

		return OptionalLong.empty();
	}

	public OptionalLong visit(IRLabel l) {
		return OptionalLong.empty();
	}

	public OptionalLong visit(IRMem m) {
        OptionalLong eol = m.expr.accept(this);

        if (eol.isPresent()) {
            m.expr = new IRConst(eol.getAsLong());
        }

		return OptionalLong.empty();
	}

	public OptionalLong visit(IRMove m) {
		OptionalLong tol = m.target.accept(this);
        OptionalLong sol = m.src.accept(this);

        if (tol.isPresent()) {
            m.target = new IRConst(tol.getAsLong());
        } 

        if (sol.isPresent()) {
            m.src = new IRConst(sol.getAsLong());
        }

        return OptionalLong.empty();
	}

	public OptionalLong visit(IRName n) {
		return OptionalLong.empty();
	}

	public OptionalLong visit(IRReturn r) {
		List<IRNode> children = new ArrayList<IRNode>();
        for (IRNode n : r.rets) {
            OptionalLong ol = n.accept(this);
            if (ol.isPresent()) {
                children.add(new IRConst(ol.getAsLong()));
            } else {
                children.add(n);
            }
        }
        r.rets = children;
        return OptionalLong.empty();
	}

	public OptionalLong visit(IRSeq s) {
        for (IRNode n : s.stmts) {
            OptionalLong ol = n.accept(this);
        }
        
        return OptionalLong.empty();
	}

	public OptionalLong visit(IRTemp t) {
		return OptionalLong.empty();
	}
}