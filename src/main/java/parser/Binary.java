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

    private static final int LHS = 0;
    private static final int RHS = 1;

    protected BinaryType btype;

    public Binary(BinaryType type, Expression lhs, Expression rhs) {
        this.btype = type; 
        this.children = new ArrayList<>(); 
        this.children.add(lhs);
        this.children.add(rhs);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( " + btype.toString() + " ");
        sb.append(children.get(LHS).toString() + " ");
        sb.append(children.get(RHS).toString() + ")");
        return sb.toString();
    }
}
