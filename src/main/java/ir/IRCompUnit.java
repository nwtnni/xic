package ir;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An intermediate representation for a compilation unit
 */
public class IRCompUnit extends IRNode {
    private String name;
    private Map<String, IRFuncDecl> functions;

    public IRCompUnit(String name) {
        this.name = name;
        functions = new LinkedHashMap<>();
    }

    public IRCompUnit(String name, Map<String, IRFuncDecl> functions) {
        this.name = name;
        this.functions = functions;
    }

    public void appendFunc(IRFuncDecl func) {
        functions.put(func.name(), func);
    }

    public String name() {
        return name;
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
