package optimize;

import ir.*;

public class IRGraphFactory<E> extends IRVisitor<IRNode> {

    public IRGraph<E> cfg;

    
    public IRNode visit(IRCompUnit c) {
        return null;
    }

    public IRNode visit(IRFuncDecl f) {
        return null;
    }

    public IRNode visit(IRSeq s) {
        return null;
    }

    public IRNode visit(IRReturn r) {
        return null;
    }

    public IRNode visit(IRCJump c) {
        return null;
    }

    public IRNode visit(IRJump j) {
        return null;
    }

    public IRNode visit(IRLabel l) {
        return null;
    }

    public IRNode visit(IRMove m) {
        return null;
    }
    
}