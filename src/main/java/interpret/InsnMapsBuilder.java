package interpret;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xic.XicInternalException;
import ir.*;

public class InsnMapsBuilder extends IRVisitor<IRNode> {
    private Map<String, Long> nameToIndex;
    private Map<Long, IRNode> indexToInsn;
    private List<String> ctors;

    private long index;

    public InsnMapsBuilder() {
        nameToIndex = new HashMap<>();
        indexToInsn = new HashMap<>();
        ctors = new LinkedList<>();
        index = 0;
    }

    public Map<String, Long> nameToIndex() {
        return nameToIndex;
    }

    public Map<Long, IRNode> indexToInsn() {
        return indexToInsn;
    }

    public List<String> ctors() {
        return ctors;
    }

    public void addInsn(IRNode n) {
        indexToInsn.put(index, n);
        index++;
    }

    public void addNameToCurrentIndex(String name) {
        if (nameToIndex.containsKey(name)) {
            throw XicInternalException.internal("Error - encountered "
                    + "duplicate name " + name
                    + " in the IR tree -- go fix the generator.");
        }
        nameToIndex.put(name, index);
    }
    
    /*
     * Visitor methods
     */

    public IRNode visit(IRCompUnit c) {
        for (IRNode n : c.functions.values()) {
            n.accept(this);
        }
        addInsn(c);
        return c;
    }

    public IRNode visit(IRFuncDecl f) {
        addNameToCurrentIndex(f.name);
        addInsn(f);
        f.body.accept(this);
        return f;
    }

    public IRNode visit(IRSeq s) {
        for (IRNode n : s.stmts()) {
            n.accept(this);
        }
        addInsn(s);
        return s;
    }

    public IRNode visit(IRESeq e) {
        e.stmt().accept(this);
        e.expr().accept(this);
        addInsn(e);
        return e;
    }

    public IRNode visit(IRExp e) {
        e.expr.accept(this);
        addInsn(e);
        return e;
    }

    public IRNode visit(IRCall c) {
        c.target().accept(this);
        for (IRNode n : c.args()) {
            n.accept(this);
        }
        addInsn(c);
        return c;
    }

    public IRNode visit(IRReturn r) {
        for (IRNode n : r.rets) {
            n.accept(this);
        }
        addInsn(r);
        return r;
    }

    public IRNode visit(IRCJump c) {
        c.cond.accept(this);
        addInsn(c);
        return c;
    }

    public IRNode visit(IRJump j) {
        j.target.accept(this);
        addInsn(j);
        return j;
    }
    
    public IRNode visit(IRName n) {
        addInsn(n);
        return n;
    }

    public IRNode visit(IRLabel l) {
        addNameToCurrentIndex(l.name);
        addInsn(l);
        return l;
    }

    public IRNode visit(IRTemp t) {
        addInsn(t);
        return t;
    }
    
    public IRNode visit(IRMem m) {
        m.expr.accept(this);
        addInsn(m);
        return m;
    }

    public IRNode visit(IRMove m) {
        m.target.accept(this);
        m.src.accept(this);
        addInsn(m);
        return m;
    }

    public IRNode visit(IRBinOp b) {
        b.left.accept(this);
        b.right.accept(this);
        addInsn(b);
        return b;
    }
    
    public IRNode visit(IRConst c) {
        addInsn(c);
        return c;
    }
}
