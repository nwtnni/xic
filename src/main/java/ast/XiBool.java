package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiBool extends Node {

    public boolean value;

    public XiBool(Location location, boolean value) {
        this.location = location;
        this.value = value;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
