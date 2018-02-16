package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class If extends Node {

    public enum Kind {
        IF, IF_ELSE 
    }

    public Kind kind;
    public Node guard;
    public Node block;
    public Node elseBlock;

    public If(Location location, Node guard, Node block) {
        this.kind = Kind.IF;
        this.location = location;
        this.guard = guard; 
        this.block = block;
        this.elseBlock = null; 
    }

    public If(Location location, Node guard, Node block, Node elseBlock) {
        this.kind = Kind.IF_ELSE;
        this.location = location;
        this.guard = guard; 
        this.block = block;
        this.elseBlock = elseBlock; 
    }

    public boolean hasElse() {
        return kind == Kind.IF_ELSE; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
