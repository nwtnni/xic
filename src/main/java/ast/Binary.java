package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

import java.util.ArrayList;

public class Binary extends Node {

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

    public Binary(Location location, Kind kind, Node lhs, Node rhs) {
        this.location = location;
        this.kind = kind;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }

    public boolean isBool() {
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
        assert false;
        return false;
    }

    public boolean isInt() {
        return !(kind == Kind.AND || kind == Kind.OR);
    }

    public boolean isList() {
        return kind == Kind.PLUS;
    }

    public boolean returnsBool() {
        return isBool();         
    }

    public boolean returnsInt() {
        return !isBool(); 
    }

    public boolean returnsList() {
        return isList();
    }
}
