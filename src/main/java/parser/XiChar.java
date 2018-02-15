package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiChar extends Expression {

    public char value;

    public XiChar(Location location, char value) {
        this.location = location;
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
