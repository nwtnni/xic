package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// An Xi integer
public class XiInt extends Node {

    public long value;
    public String literal;
    public boolean negated;

    public XiInt(Location location, long value, String literal, boolean negated) {
        this.location = location;
        this.value = value;
        this.literal = literal;
        this.negated = negated;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
