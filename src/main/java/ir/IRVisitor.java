package ir;

import java.util.List;
import java.util.ArrayList;

public abstract class IRVisitor<T> {

    /*
     * Psuedo-visit method for visiting a list of nodes.
     */
    public List<T> visit(List<IRNode> nodes) {
        List<T> t = new ArrayList<>();
        for (IRNode n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }
    
    public T visit(IRCompUnit c) {
        return null;
    }

    public T visit(IRFuncDecl f) {
        return null;
    }

    public T visit(IRSeq s) {
        return null;
    }

    public T visit(IRESeq e) {
        return null;
    }

    public T visit(IRExp e) {
        return null;
    }

    public T visit(IRCall c) {
        return null;
    }

    public T visit(IRReturn r) {
        return null;
    }

    public T visit(IRCJump c) {
        return null;
    }

    public T visit(IRJump j) {
        return null;
    }
    
    public T visit(IRName n) {
        return null;
    }

    public T visit(IRLabel l) {
        return null;
    }

    public T visit(IRTemp t) {
        return null;
    }
    
    public T visit(IRMem m) {
        return null;
    }

    public T visit(IRMove m) {
        return null;
    }

    public T visit(IRBinOp b) {
        return null;
    }
    
    public T visit(IRConst c) {
        return null;
    }

}
