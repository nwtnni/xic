package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiString extends Expression {

    public String value;

    public XiString(Location location, String value) {
        this.location = location;
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
