package ir;

import java.util.List;

/** An IR function declaration */
public class IRFuncDecl extends IRNode {
    private String name;
    private IRSeq body;

    public IRFuncDecl(String name) {
        this.name = name;
        this.body = new IRSeq();
    }

    public IRFuncDecl(String name, IRSeq body) {
        this.name = name;
        this.body = body;
    }

    public String name() {
        return name;
    }

    public IRSeq body() {
        return body;
    }

    public List<IRStmt> setBody(List<IRStmt> s) {
        return body.setStmts(s);
    }

    public boolean add(IRStmt s) {
        return body.add(s);
    }

    public void add(int index, IRStmt s) {
        body.add(index, s);
    }

    public IRStmt set(int index, IRStmt s) {
        return body.set(index, s);
    }

    public IRStmt get(int index) {
        return body.get(index);
    }

    public int size() {
        return body.size();
    }

    @Override
    public String label() {
        return "FUNC " + name;
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
