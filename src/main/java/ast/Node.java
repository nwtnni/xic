package ast;
 
import java_cup.runtime.ComplexSymbolFactory.Location;

import type.Type;

public abstract class Node {

    public Location location;
    public Type type;

    public abstract <T> T accept(Visitor<T> v);

}
