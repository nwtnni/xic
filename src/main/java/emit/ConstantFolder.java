package emit;

import java.util.OptionalLong;
import ir.*;

public class ConstantFolder extends IRVisitor<OptionalLong> {

    IRNode tree;

	public OptionalLong visit(IRBinOp b) {
		IRNode lt = b.left.accept(this);
        IRNode rt = b.right.accept(this);
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
                c = lt.getAsLong() *>> rt.getAsLong();
                break;
            case DIV:
                c = lt.getAsLong() / rt.getAsLong();
                break;
            case MOD:
                c = lt.getAsLong() % rt.getAsLong();
                break;
            case AND:
                c = lt.getAsLong() && rt.getAsLong();
                break;
            case OR:
                c = lt.getAsLong() || rt.getAsLong();
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
                c = lt.getAsLong() == rt.getAsLong();
                break;
            case NEQ:
                c = lt.getAsLong() != rt.getAsLong();
                break;
            case LT:
                c = lt.getAsLong() < rt.getAsLong();
                break;
            case GT:
                rc = lt.getAsLong() > rt.getAsLong();
                break;
            case LEQ:
                c = lt.getAsLong() <= rt.getAsLong();
                break;
            case GEQ:
                c = lt.getAsLong() >= rt.getAsLong();
                break;
            default: return (new OptionalLong()).empty();
                break;
            }
        }
        return new IRBinOp(b.type, lt, rt);
	}
	
	public OptionalLong visit(IRCall c) {
		return null;
	}

	public OptionalLong visit(IRCJump c) {
		return null;
	}

	public OptionalLong visit(IRJump j) {
		return null;
	}
	
	public OptionalLong visit(IRCompUnit c) {
		return null;
	}

	public OptionalLong visit(IRConst c) {
		return null;
	}

	public OptionalLong visit(IRESeq e) {
		return null;
	}

	public OptionalLong visit(IRExp e) {
		return null;
	}

	public OptionalLong visit(IRFuncDecl f) {
		return null;
	}

	public OptionalLong visit(IRLabel l) {
		return null;
	}

	public OptionalLong visit(IRMem m) {
		return null;
	}

	public OptionalLong visit(IRMove m) {
		return null;
	}

	public OptionalLong visit(IRName n) {
		return null;
	}

	public OptionalLong visit(IRReturn r) {
		return null;
	}

	public OptionalLong visit(IRSeq s) {
		return null;
	}

	public OptionalLong visit(IRTemp t) {
		return null;
	}
}