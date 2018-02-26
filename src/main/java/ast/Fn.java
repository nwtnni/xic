package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

import java.util.ArrayList;

// Function Declaration
public class Fn extends Node {
    
    public enum Kind {
        FN, FN_HEADER,
        PROC, PROC_HEADER
    }

    public Kind kind;
    public String id;
    public Node args;
    public Node returns;
    public Node block;

    public Fn(Location location, String id, Node args, Node returns) {
        this.kind = Kind.FN_HEADER;
        this.location = location;
        this.id = id;  
        this.args = args;
        this.returns = returns;
        this.block = null;
    }

    public Fn(Location location, String id, Node args) {
        this.kind = Kind.PROC_HEADER; 
        this.location = location;
        this.id = id;  
        this.args = args;
        this.returns = new Multiple(location, new ArrayList<>(), Multiple.Kind.FN_RETURNS);
        this.block = null;
    }

    public Fn(Location location, Fn f, Node block) {
        if (f.kind == Kind.FN_HEADER) {
            this.kind = Kind.FN;
        }
        else if (f.kind == Kind.PROC_HEADER) {
            this.kind = Kind.PROC;
        }
        else {
            // TODO
            // Should Have error handling
            // throw new Exception("WHAT");
        }
        this.location = location;
        this.id = f.id;
        this.args = f.args;
        this.returns = f.returns;
        this.block = block;
    }

    public boolean isFn() {
        return kind == Kind.FN || kind == Kind.FN_HEADER; 
    }

    public boolean isDef() {
        return kind == Kind.FN || kind == Kind.PROC;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
