package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Return extends Statement {

    public Node value;

    public Return(Location location, Node value) {
        this.location = location;
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
