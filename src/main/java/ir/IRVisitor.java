package ir;

/** Base class IR level template for visitor pattern. */
public abstract class IRVisitor<T> {

    /*
     * Top level nodes
     */
    
    public T visit(IRCompUnit c) {
        return null;
    }

    public T visit(IRFuncDecl f) {
        return null;
    }

    /*
     * Statement nodes
     */

    public T visit(IRExp e) {
        return null;
    }

    public T visit(IRCJump c) {
        return null;
    }

    public T visit(IRJump j) {
        return null;
    }

    public T visit(IRLabel l) {
        return null;
    }

    public T visit(IRMove m) {
        return null;
    }

    public T visit(IRReturn r) {
        return null;
    }

    public T visit(IRSeq s) {
        return null;
    }

    /*
     * Expression nodes
     */

    public T visit(IRBinOp b) {
        return null;
    }

    public T visit(IRCall c) {
        return null;
    }
    
    public T visit(IRConst c) {
        return null;
    }

    public T visit(IRESeq e) {
        return null;
    }
    
    public T visit(IRMem m) {
        return null;
    }

    public T visit(IRName n) {
        return null;
    }

    public T visit(IRTemp t) {
        return null;
    }
    
}
