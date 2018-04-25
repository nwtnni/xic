package optimize;

import java.util.HashMap;
import java.util.Map;

import assemble.*;
import assemble.instructions.*;

public class ASAGraphFactory<E> extends InsVisitor<Instr> {

    public ASAGraphFactory(CompUnit compUnit, ASAEdgeFactory<E> edgeFactory) {
        this.compUnit = compUnit;
        this.edgeFactory = edgeFactory;
    }

    /** The compilation unit to generate CFGs from. */
    private CompUnit compUnit;

    /** The edge factory used to contruct edges in the CFG */
    private ASAEdgeFactory<E> edgeFactory;

    /** Current CFG being constructed. */
    private ASAGraph<E> cfg;

    /** Previous statement in the assembly. */
    private Instr prev;

    /** Returns the list of CFGs for the compilation unit. */
    public Map<String, ASAGraph<E>> getCfgs() {
        Map<String, ASAGraph<E>> fns = new HashMap<>();
        for (FuncDecl fn : compUnit.fns) {
            // Generate graph
            fns.put(fn.name, cfg);
        }
        return fns;
    }

    public void toCfg(FuncDecl fn) {
        cfg = new ASAGraph<>(fn.sourceName, fn.name, fn.stmts.get(0), edgeFactory);
        for (Instr ins : fn.stmts) {
            cfg.addVertex(ins);

            if (prev != null) {
                cfg.addEdge(prev, ins);
            }

            prev = ins.accept(this);
        }
    }

    public Instr visit(BinOp i) {
        return i;
    }

    public Instr visit(Call i) {
        return i;
    }

    public Instr visit(Cmp i) {
        return i;
    }

    public Instr visit(Cqo i) {
        return i;
    }

    public Instr visit(DivMul i) {
        return i;
    }

    public Instr visit(Jcc i) {
        cfg.addVertex(i.target);
        cfg.addEdge(i, i.target);
        return i;
    }

    public Instr visit(Jmp i) {
        // TODO: only support jump to label
        if (i.hasLabel()) {
            cfg.addVertex(i.label);
            cfg.addEdge(i, i.label);
        }
        return null;
    }

    public Instr visit(Label i) {
        return i;
    }

    public Instr visit(Lea i) {
        return i;
    }

    public Instr visit(Mov i) {
        return i;
    }

    public Instr visit(Pop i) {
        return i;
    }

    public Instr visit(Push i) {
        return i;
    }


    public Instr visit(Ret i) {
        return null;
    }

    public Instr visit(Set i) {
        return i;
    }

    public Instr visit(Text i) {
        return i;
    }

}