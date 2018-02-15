package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import java.util.ArrayList;

public class Binary extends Node {

    public enum BinaryType {
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

        private BinaryType(String token) {
            this.token = token;
        }

        public String toString() {
            return token; 
        }
    }

    public BinaryType btype;
    public Node lhs;
    public Node rhs;

    public Binary(Location location, BinaryType type, Node lhs, Node rhs) {
        this.location = location;
        this.btype = type; 
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
