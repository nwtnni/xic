package ir;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An intermediate representation for a compilation unit
 */
public class IRCompUnit extends IRNode {
    public String name;
    public Map<String, IRFuncDecl> functions;

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

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     boolean modified = false;

    //     Map<String, IRFuncDecl> results = new LinkedHashMap<>();
    //     for (IRFuncDecl func : functions.values()) {
    //         IRFuncDecl newFunc = (IRFuncDecl) v.visit(this, func);
    //         if (newFunc != func) modified = true;
    //         results.put(newFunc.name(), newFunc);
    //     }

    //     if (modified) return v.nodeFactory().IRCompUnit(name, results);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     for (IRFuncDecl func : functions.values())
    //         result = v.bind(result, v.visit(func));
    //     return result;
    // }
    
    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}