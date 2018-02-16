package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiBool extends Node {

    public boolean value;

    public XiBool(Location location, boolean value) {
        this.location = location;
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
