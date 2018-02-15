package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class ProcedureCall extends Statement {

    public Node id;

    public ProcedureCall(Location location, Node id) {
        this.location = location;
        this.id = id;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
