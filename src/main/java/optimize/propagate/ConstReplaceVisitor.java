package optimize.propagate;

import java.util.Map;
import java.util.Optional;

import ir.*;
import optimize.graph.*;

public class ConstReplaceVisitor extends IRVisitor<Optional<IRConst>> {

    /**
     * Runs the available constants analysis and updates the CFG with constant propagation.
     */
    public static void runConstantProp(IRGraph<Map<IRTemp, Optional<IRConst>>> cfg) {
        Map<IRStmt, Map<IRTemp, Optional<IRConst>>> consts = ConstWorklist.computeAvailableConsts(cfg);
        
        ConstReplaceVisitor visitor = new ConstReplaceVisitor(consts);

        for (IRStmt st : cfg.vertexSet()) {
            visitor.currentConstMap = visitor.avaliableConsts.get(st);
            st.accept(visitor);
        }

    }

    private ConstReplaceVisitor(Map<IRStmt, Map<IRTemp, Optional<IRConst>>> ac) {
        this.avaliableConsts = ac;
    }

    Map<IRStmt, Map<IRTemp, Optional<IRConst>>> avaliableConsts;

    Map<IRTemp, Optional<IRConst>> currentConstMap;

    /*
     * Statement nodes
     */

    public Optional<IRConst> visit(IRExp e) {
        Optional<IRConst> c = e.expr().accept(this);
        if (c.isPresent()) {
            e.expr = c.get();
        }
        return null;
    }

    public Optional<IRConst> visit(IRCJump c) {
        Optional<IRConst> cs = c.cond().accept(this);
        if (cs.isPresent()) {
            c.cond = cs.get();
        }
        return null;
    }

    public Optional<IRConst> visit(IRJump j) {
        // TODO: update when jumps are not to labels
        return null;
    }

    public Optional<IRConst> visit(IRLabel l) {
        // No action needed
        return null;
    }

    public Optional<IRConst> visit(IRMove m) {
        Optional<IRConst> c = m.src().accept(this);
        if (c.isPresent()) {
            m.src = c.get();
        }
        return null;
    }

    public Optional<IRConst> visit(IRReturn r) {
        for (int i = 0; i < r.size(); i++) {
            Optional<IRConst> c = r.get(i).accept(this);
            if (c.isPresent()) {
                r.set(i, c.get());
            }
        }
        return null;
    }

    public Optional<IRConst> visit(IRSeq s) {
        // Does not appear in CFG
        return null;
    }

    /*
     * Expression nodes
     */

    public Optional<IRConst> visit(IRBinOp b) {
        Optional<IRConst> l = b.left().accept(this);
        if (l.isPresent()) {
            b.left = l.get();
        }
        Optional<IRConst> r = b.right().accept(this);
        if (r.isPresent()) {
            b.right = r.get();
        }
        return Optional.empty();
    }

    public Optional<IRConst> visit(IRCall c) {
        for (int i = 0; i < c.size(); i++) {
            Optional<IRConst> cs = c.get(i).accept(this);
            if (cs.isPresent()) {
                c.set(i, cs.get());
            }
        }
        return Optional.empty();
    }
    
    public Optional<IRConst> visit(IRConst c) {
        // No action needed
        return Optional.empty();
    }

    public Optional<IRConst> visit(IRESeq e) {
        // Should not appear in CFG
        assert false;
        return Optional.empty();
    }
    
    public Optional<IRConst> visit(IRMem m) {
        Optional<IRConst> c = m.expr().accept(this);
        if (c.isPresent()) {
            m.expr = c.get();
        }
        return Optional.empty();
    }

    public Optional<IRConst> visit(IRName n) {
        // No action needed
        return Optional.empty();
    }

    public Optional<IRConst> visit(IRTemp t) {
        // Return constant to replace with otherwise empty
        if (currentConstMap.containsKey(t) && currentConstMap.get(t).isPresent()) {
            return currentConstMap.get(t);
        }
        return Optional.empty();
    }
    
}
