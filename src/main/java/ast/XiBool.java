package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// An Xi primitive boolean
public class XiBool extends Node {

    public boolean value;

    public XiBool(Location location, boolean value) {
        this.location = location;
        this.value = value;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
