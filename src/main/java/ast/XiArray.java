package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class XiArray extends Node {

    public ArrayList<Node> values;

    public XiArray(Location location, ArrayList<Node> values) {
        this.location = location;
        this.values = values;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
