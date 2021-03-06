package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A unary operator
public class XiUnary extends Expr {
    
    public enum Kind {
        INEGATE("-"),
        LNEGATE("!");
    
        private String token;

        private Kind(String token) {
            this.token = token; 
        }

        public String toString() {
            return token; 
        }
    }

    public Kind kind;
    public Node child; 

    public XiUnary(Location location, Kind kind, Node child) {
        this.location = location;
        this.kind = kind; 
        this.child = child;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }

    public boolean isLogical() {
        return kind == Kind.LNEGATE; 
    }
}
