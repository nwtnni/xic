package ir;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import emit.ABIContext;

/**
 * An intermediate representation for a compilation unit
 */
public class IRCompUnit extends IRNode {
    
    /** Compilation unit source file name. */
    private String name;

    /** Context for the unit. */
    private ABIContext context;

    /** Compilation unit global variables. */
    private List<IRStmt> globals;

    /** Map of ABI names to function declarations. */
    private Map<String, IRFuncDecl> functions;

    public IRCompUnit(String name, ABIContext context) {
        this.name = name;
        this.context = context;
        this.globals = new ArrayList<>();
        functions = new LinkedHashMap<>();
    }

    public IRCompUnit(String name, ABIContext context, List<IRStmt> globals, Map<String, IRFuncDecl> functions) {
        this.name = name;
        this.context = context;
        this.globals = globals;
        this.functions = functions;
    }

    public void appendFunc(IRFuncDecl func) {
        functions.put(func.name(), func);
    }

    public String name() {
        return name;
    }

    public ABIContext context() {
        return context;
    }

    public List<IRStmt> globals() {
        return globals;
    }

    public Map<String, IRFuncDecl> functions() {
        return functions;
    }

    public IRFuncDecl getFunction(String name) {
        return functions.get(name);
    }

    @Override
    public String label() {
        return "COMPUNIT";
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
