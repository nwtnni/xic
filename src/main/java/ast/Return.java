package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Return extends Node {

    public enum Kind {
        VALUE, VOID  
    }

    public Kind kind;
    public Node value;

    public Return(Location location, Node value) {
        this.kind = Kind.VALUE;
        this.location = location;
        this.value = value;
    }

    public Return(Location location) {
        this.kind = Kind.VOID; 
        this.location = location;
        this.value = null;
    }

    public boolean hasValue() {
        return kind == Kind.VALUE; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
