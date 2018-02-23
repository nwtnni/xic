package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiInt extends Node {

    public long value;

    public XiInt(Location location, long value) {
        this.location = location;
        this.value = value; 
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
