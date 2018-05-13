package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// Binary Operation
public class XiBinary extends Expr {

    public enum Kind {
        TIMES("*"),
        HIGH_TIMES("*>>"),
        DIVISION("/"),
        MODULO("%"),
        PLUS("+"),
        MINUS("-"),
        LT("<"),
        LE("<="),
        GE(">="),
        GT(">"),
        EQ("=="),
        NE("!="),
        AND("&"),
        OR("|");

        private String token;

        private Kind(String token) {
            this.token = token;
        }

        public String toString() {
            return token;
        }
    }

    public Kind kind;
    public Node lhs;
    public Node rhs;

    public XiBinary(Location location, Kind kind, Node lhs, Node rhs) {
        this.location = location;
        this.kind = kind;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }

    public boolean acceptsBool() {
        switch (kind) {
            case TIMES:
            case HIGH_TIMES:
            case DIVISION:
            case MODULO:
            case PLUS:
            case MINUS:
            case LT:
            case LE:
            case GE:
            case GT:
                return false;
            case EQ:
            case NE:
            case AND:
            case OR:
                return true;
        }
        // Unreachable
        assert false;
        return false;
    }

    public boolean acceptsInt() {
        return !(kind == Kind.AND || kind == Kind.OR);
    }

    public boolean acceptsList() {
        return kind == Kind.PLUS || kind == Kind.EQ || kind == Kind.NE;
    }

    public boolean returnsBool() {
        switch (kind) {
            case TIMES:
            case HIGH_TIMES:
            case DIVISION:
            case MODULO:
            case PLUS:
            case MINUS:
                return false;
            case LT:
            case LE:
            case GE:
            case GT:
            case EQ:
            case NE:
            case AND:
            case OR:
                return true;
        }
        // Unreachable
        assert false;
        return false;       
    }

    public boolean returnsList() {
        return kind == Kind.PLUS;
    }
}
