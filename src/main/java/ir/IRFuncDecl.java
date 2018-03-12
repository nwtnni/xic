package ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/** An IR function declaration */
public class IRFuncDecl extends IRNode {
    public String name;
    public IRNode body;

    public IRFuncDecl(String name, IRNode body) {
        this.name = name;
        this.body = body;
    }

    public String name() {
        return name;
    }

    public IRNode body() {
        return body;
    }

    @Override
    public String label() {
        return "FUNC " + name;
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     IRStmt stmt = (IRStmt) v.visit(this, body);

    //     if (stmt != body) return v.nodeFactory().IRFuncDecl(name, stmt);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     result = v.bind(result, v.visit(body));
    //     return result;
    // }

    // @Override
    // public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
    //     v.addNameToCurrentIndex(name);
    //     v.addInsn(this);
    //     return v;
    // }

    // @Override
    // public IRNode buildInsnMaps(InsnMapsBuilder v) {
    //     return this;
    // }
    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
