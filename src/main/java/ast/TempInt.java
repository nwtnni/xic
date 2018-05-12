package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// An temporary node to hold an integer literal 
public class TempInt extends Node {

    public String literal;

    public TempInt(Location location, String literal) {
        this.location = location;
        this.literal = literal; 
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        // Should never appear in an AST
        assert false;
        return null;
    }
}
