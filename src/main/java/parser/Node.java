package parser;
 
import java_cup.runtime.ComplexSymbolFactory.Location;
import java.util.ArrayList;

public abstract class Node {

    public Location location;

    public abstract void accept(Visitor v);

}
