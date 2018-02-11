package parser;

import java.util.ArrayList;

public class Unary extends Expression {
    
    public enum UnaryType {
        INEGATE("-"),
        LNEGATE("!");
    
        private String token;

        private UnaryType(String token) {
            this.token = token; 
        }

        public String toString() {
            return token; 
        }
    }

    public static final int RHS = 0;
    private UnaryType utype;

    public Unary(UnaryType utype, Expression rhs) {
        this.utype = utype; 
        this.children = new ArrayList<>();
        this.children.add(rhs);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( " + utype.toString());
        sb.append(" " + children.get(RHS).toString() + ")");
        return sb.toString();
    }
}
