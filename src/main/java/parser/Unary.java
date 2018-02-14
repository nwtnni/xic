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

    public UnaryType utype;
    public Node child; 

    public Unary(UnaryType utype, Node child) {
        this.utype = utype; 
        this.child = child;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
