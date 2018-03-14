package ast;

import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// Used to hold tuples (multiple assign/multiple return/function args/etc)
public class Multiple extends Node {

    public enum Kind {
        ASSIGN, RETURN, FN_ARGS, FN_RETURNS, FN_CALL,
    }

    public Kind kind;
    public List<Node> values;

    public Multiple(Location location, List<Node> values, Kind kind) {
        this.kind = kind;
        this.location = location;
        this.values = values;
    }

    public boolean isAssign() {
        return kind == Kind.ASSIGN; 
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
