package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
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

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
