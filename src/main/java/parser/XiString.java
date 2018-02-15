package parser;

import java.util.ArrayList;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiString extends Expression {

    public String escaped;
    public ArrayList<Long> value;

    public XiString(Location location, String escaped, ArrayList<Long> value) {
        this.location = location;
        this.escaped = escaped;
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
