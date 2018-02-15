package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class Function extends Node {
    
    private enum Kind {
        FUNCTION, FUNCTION_HEADER,
        PROCEDURE, PROCEDURE_HEADER
    }

    private Kind kind;
    public String id;
    public ArrayList<Node> args;
    public ArrayList<Node> types;
    public Node block;

    public Function(Location location, String id, ArrayList<Node> args, ArrayList<Node> types, Node block) {
        this.kind = Kind.FUNCTION;
        this.location = location;
        this.id = id;  
        this.args = args;
        this.types = types;
        this.block = block;
    }

    public Function(Location location, String id, ArrayList<Node> args, ArrayList<Node> types) {
        this.kind = Kind.FUNCTION_HEADER;
        this.location = location;
        this.id = id;  
        this.args = args;
        this.types = types;
        this.block = null;
    }

    public Function(Location location, String id, ArrayList<Node> args, Node block) {
        this.kind = Kind.PROCEDURE;
        this.location = location;
        this.id = id;  
        this.args = args;
        this.types = null;
        this.block = block;
    }

    public Function(Location location, String id, ArrayList<Node> args) {
        this.kind = Kind.PROCEDURE_HEADER; 
        this.location = location;
        this.id = id;  
        this.args = args;
        this.types = null;
        this.block = null;
    }

    public Function(Location location, Function f, Node block) {
        this.kind = f.kind;
        this.location = location;
        this.id = f.id;
        this.args = f.args;
        this.types = f.types;
        this.block = block;
    }

    public boolean isFunction() {
        return kind == Kind.FUNCTION || kind == Kind.FUNCTION_HEADER; 
    }

    public boolean isDefinition() {
        return kind == Kind.FUNCTION || kind == Kind.PROCEDURE;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
