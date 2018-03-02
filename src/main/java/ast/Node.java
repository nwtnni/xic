package ast;
 
import java_cup.runtime.ComplexSymbolFactory.Location;

import type.Type;
import xic.XicException;

// A generic AST node.
public abstract class Node {

    public Location location;
    public Type type;

    public abstract <T> T accept(Visitor<T> v) throws XicException;

}
