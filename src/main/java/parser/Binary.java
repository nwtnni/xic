package parser;

import java.util.ArrayList;

public class Binary extends Expression {

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

    public Binary(BinaryType type, Node lhs, Node rhs) {
        this.btype = type; 
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}