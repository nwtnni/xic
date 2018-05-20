package ir;

import java.util.List;

/** An IR function declaration */
public class IRFuncDecl extends IRNode {
    /** The original function name from source. */
    private String sourceName;

    /** The mangled function name. */
    private String name;

    /** Number of arguments. */
    private int args;

    /** Number of returns. */
    private int rets;

    /** The function body. */
    private IRSeq body;

    public IRFuncDecl(String sourceName, String name, int args, int rets) {
        this.sourceName = sourceName;
        this.name = name;
        this.args = args;
        this.rets = rets;
        this.body = new IRSeq();
    }

    public IRFuncDecl(String sourceName, String name, int args, int rets, IRSeq body) {
        this.sourceName = sourceName;
        this.name = name;
        this.args = args;
        this.rets = rets;
        this.body = body;
    }

    public String sourceName() {
        return sourceName;
    }

    public String name() {
        return name;
    }

    public int args() {
        return args;
    }

    public int rets() {
        return rets;
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

    public boolean addAll(List<IRStmt> s) {
        return body.addAll(s);
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
