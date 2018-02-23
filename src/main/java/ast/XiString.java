package ast;

import java.util.ArrayList;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiString extends Node {

    public String escaped;
    public ArrayList<Long> value;

    public XiString(Location location, String escaped, ArrayList<Long> value) {
        this.location = location;
        this.escaped = escaped;
        this.value = value;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
