package emit;

import java.util.OptionalLong;
import java.util.List;
import java.util.ArrayList;

import ir.*;
import java.math.BigInteger;

/**
 * Main implementation of IR to IR constant folding optimizations.
 * This optimization only folds primitive constants and checks a
 * specific case for equality comparison on array literals.
 */
public class ConstantFolder extends IRVisitor<OptionalLong> {

    /**
     * Factory method to constant fold an IR AST.
     * 
     * @param ast
     */
    public static void constantFold(IRNode ast) {
        ast.accept(new ConstantFolder());
    }

    public OptionalLong visit(IRBinOp b) {

        // Array literal equality checks
        if (b.left() instanceof IRESeq && b.right() instanceof IRESeq) {
            IRESeq l = (IRESeq) b.left();
            IRESeq r = (IRESeq) b.right();
            if (l.hasValues() && r.hasValues()) {
                switch (b.type()) {
                    case NEQ:
                        return OptionalLong.of(1);
                    case EQ:
                        return OptionalLong.of(0);
                    default:
                        break;
                }
            }
        }

        OptionalLong ltol = b.left().accept(this);
        OptionalLong rtol = b.right().accept(this);

        // Check boolean and arithmetic
        if (ltol.isPresent() && rtol.isPresent()) {
            long c;
            long lt = ltol.getAsLong();
            long rt = rtol.getAsLong();
            switch (b.type()) {
            case ADD: 
                c = lt + rt;
                break;
            case SUB:
                c = lt - rt;
                break;
            case MUL:
                c = lt * rt;
                break;
            case HMUL:
                c = BigInteger.valueOf(lt).
                    multiply(BigInteger.valueOf(rt)).shiftRight(64).longValue();
                break;
            case DIV:
                if (rt == 0) {
                    b.left = new IRConst(ltol.getAsLong());
                    b.right = new IRConst(rtol.getAsLong());
                    return OptionalLong.empty();
                }
                c = lt / rt;
                break;
            case MOD:
                if (rt == 0) {
                    return OptionalLong.empty();
                }
                c = lt % rt;
                break;
            case AND:
                c = ((lt == 1) && (rt == 1)) ? 1 : 0;
                break;
            case OR:
                c = ((lt == 1) || (rt == 1)) ? 1 : 0;
                break;
            case XOR:
                c = lt ^ rt;
                break;
            case LSHIFT:
                c = lt << rt;
                break;
            case RSHIFT:
                c = lt >> rt;
                break;
            case ARSHIFT:
                c = lt >>> rt;
                break;
            case EQ:
                c = (lt == rt) ? 1 : 0;
                break;
            case NEQ:
                c = (lt != rt) ? 1 : 0;
                break;
            case LT:
                c = (lt < rt) ? 1 : 0;
                break;
            case GT:
                c = (lt > rt) ? 1 : 0;
                break;
            case LEQ:
                c = (lt <= rt) ? 1 : 0;
                break;
            case GEQ:
                c = (lt >= rt) ? 1 : 0;
                break;
            default: // unreachable
                assert false;
                return null;
            }
            return OptionalLong.of(c);
        }

        // Constant fold one side of array
        if (ltol.isPresent()) {
            switch (b.type()) {
                case MUL:
                case HMUL:
                    if (ltol.getAsLong() == 0) {
                        return OptionalLong.of(0);
                    }
                default:
                    b.left = new IRConst(ltol.getAsLong());
            }
        } else if (rtol.isPresent()) {
            switch (b.type()) {
                case MUL:
                case HMUL:
                    if (rtol.getAsLong() == 0) {
                        return OptionalLong.of(0);
                    }
                default:
                    b.right = new IRConst(rtol.getAsLong());
            }
        }

        return OptionalLong.empty();
        
    }
    
    public OptionalLong visit(IRCall c) {
        List<IRExpr> children = new ArrayList<>();
        for (IRExpr n : c.args()) {
            OptionalLong ol = n.accept(this);
            if (ol.isPresent()) {
                children.add(new IRConst(ol.getAsLong()));
            } else {
                children.add(n);
            }
        }
        c.setArgs(children);
        return OptionalLong.empty();
    }

    public OptionalLong visit(IRCJump c) {
        OptionalLong ol = c.cond.accept(this);
        if (ol.isPresent()) {
            c.cond = new IRConst(ol.getAsLong());
        }
        return OptionalLong.empty();
    }

    public OptionalLong visit(IRJump j) {
        OptionalLong ol = j.target().accept(this);
        if (ol.isPresent()) {
            j.setTarget(new IRConst(ol.getAsLong()));
        }
        return OptionalLong.empty();
    }
    
    public OptionalLong visit(IRCompUnit c) {
        for (IRFuncDecl fd : c.functions().values()) {
            fd.accept(this);
        }
        return OptionalLong.empty();
    }

    public OptionalLong visit(IRConst c) {
        return OptionalLong.of(c.value());
    }

    public OptionalLong visit(IRESeq e) {
        /* e.stmt cannot be constant, do not need to check */
        e.stmt().accept(this);
        OptionalLong eol = e.expr().accept(this);

        if (eol.isPresent()) {
            e.expr = new IRConst(eol.getAsLong());
        }

        return OptionalLong.empty();
    }

    public OptionalLong visit(IRExp e) {
        OptionalLong eol = e.expr().accept(this);

        if (eol.isPresent()) {
            return OptionalLong.of(eol.getAsLong());
        }

        return OptionalLong.empty();
    }

    public OptionalLong visit(IRFuncDecl f) {
        // The body is statements, will never return constants
        f.body().accept(this);

        return OptionalLong.empty();
    }

    public OptionalLong visit(IRLabel l) {
        return OptionalLong.empty();
    }

    public OptionalLong visit(IRMem m) {
        OptionalLong eol = m.expr().accept(this);

        if (eol.isPresent()) {
            m.expr = new IRConst(eol.getAsLong());
        }

        return OptionalLong.empty();
    }

    public OptionalLong visit(IRMove m) {
        OptionalLong tol = m.target().accept(this);
        OptionalLong sol = m.src().accept(this);

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
        List<IRExpr> children = new ArrayList<>();
        for (IRExpr n : r.rets()) {
            OptionalLong ol = n.accept(this);
            if (ol.isPresent()) {
                children.add(new IRConst(ol.getAsLong()));
            } else {
                children.add(n);
            }
        }
        r.setRets(children);
        return OptionalLong.empty();
    }

    public OptionalLong visit(IRSeq s) {
        for (IRNode n : s.stmts()) {
            n.accept(this);
        }
        
        return OptionalLong.empty();
    }

    public OptionalLong visit(IRTemp t) {
        return OptionalLong.empty();
    }
}