package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class If extends Node {

    public Node guard;
    public Node block;
    public Node elseBlock;

    public If(Location location, Node guard, Node block, Node elseBlock) {
        this.location = location;
        this.guard = guard; 
        this.block = block;
        this.elseBlock = elseBlock; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
