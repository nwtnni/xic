package ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a binary operation
 * OP(left, right)
 */
public class IRBinOp extends IRExpr_c {

    /**
     * Binary operators
     */
    public enum OpType {
        ADD, SUB, MUL, HMUL, DIV, MOD, AND, OR, XOR, LSHIFT, RSHIFT, ARSHIFT,
        EQ, NEQ, LT, GT, LEQ, GEQ;

        @Override
        public String toString() {
            switch (this) {
            case ADD:
                return "ADD";
            case SUB:
                return "SUB";
            case MUL:
                return "MUL";
            case HMUL:
                return "HMUL";
            case DIV:
                return "DIV";
            case MOD:
                return "MOD";
            case AND:
                return "AND";
            case OR:
                return "OR";
            case XOR:
                return "XOR";
            case LSHIFT:
                return "LSHIFT";
            case RSHIFT:
                return "RSHIFT";
            case ARSHIFT:
                return "ARSHIFT";
            case EQ:
                return "EQ";
            case NEQ:
                return "NEQ";
            case LT:
                return "LT";
            case GT:
                return "GT";
            case LEQ:
                return "LEQ";
            case GEQ:
                return "GEQ";
            }
            // Exhaustive switch statement
            assert false;
            return "";
        }
    };

    private OpType type;
    private IRExpr left, right;

    public IRBinOp(OpType type, IRExpr left, IRExpr right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public OpType opType() {
        return type;
    }

    public IRExpr left() {
        return left;
    }

    public IRExpr right() {
        return right;
    }

    @Override
    public String label() {
        return type.toString();
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom(type.toString());
        left.printSExp(p);
        right.printSExp(p);
        p.endList();
    }

}
