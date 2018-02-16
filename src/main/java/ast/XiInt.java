package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiInt extends Node {

    public long value;

    public XiInt(Location location, long value) {
        this.location = location;
        this.value = value; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}