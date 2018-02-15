package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class XiChar extends Expression {

    public String escaped;
    public long value;

    public XiChar(Location location, String escaped, long value) {
        this.location = location;
        this.escaped = escaped;
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
