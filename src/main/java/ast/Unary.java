package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Unary extends Node {
    
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

    public Unary(Location location, Kind kind, Node child) {
        this.location = location;
        this.kind = kind; 
        this.child = child;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public boolean isLogical() {
        return kind == Kind.LNEGATE; 
    }
}
