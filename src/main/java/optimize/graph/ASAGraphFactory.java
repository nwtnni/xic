package optimize.graph;

import java.util.HashMap;
import java.util.Map;

import assemble.*;
import assemble.instructions.*;
import emit.IRLabelFactory;

public class ASAGraphFactory<E> extends InsVisitor<Instr> {

    public ASAGraphFactory(CompUnit compUnit, ASAEdgeFactory<E> edgeFactory) {
        this.compUnit = compUnit;
        this.edgeFactory = edgeFactory;
    }

    private enum State { IN_BLOCK, OUT_OF_BLOCK; }

    /** The compilation unit to generate CFGs from. */
    private CompUnit compUnit;

    /** The edge factory used to contruct edges in the CFG */
    private ASAEdgeFactory<E> edgeFactory;

    /** Current CFG being constructed. */
    private ASAGraph<E> cfg;

    /** 
     * State is used to determine whether a jump needs to be injected for
     * a fall-through to a label.
     */
    private State state;

    /** Previous statement in the assembly. */
    private Instr prev;

    /** Returns the list of CFGs for the compilation unit. */
    public Map<String, ASAGraph<E>> getCfgs() {
        Map<String, ASAGraph<E>> fns = new HashMap<>();
        for (FuncDecl fn : compUnit.fns) {
            // Generate graph
            toCfg(fn);
            fns.put(fn.name, cfg);
        }
        return fns;
    }

    public void toCfg(FuncDecl fn) {
        state = State.IN_BLOCK;

        Label start = Label.label(IRLabelFactory.generate("START"));
        prev = start;
        cfg = new ASAGraph<>(fn, start, edgeFactory);

        for (Instr ins : fn.stmts) {
            // Add edge if in a block and not the first node
            if (state == State.IN_BLOCK && prev != null) {
                cfg.addVertex(ins);
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
        if (state == State.IN_BLOCK) {
            // Add edge to target
            cfg.addVertex(i.target);
            cfg.addEdge(i, i.target);
        }

        // Jcc falls through so no state change
        return i;
    }

    public Instr visit(Jmp i) {
        if (state == State.IN_BLOCK) {
            // Add edge to jump target if not the return label
            if (i.hasLabel() && !i.label.equals(cfg.originalFn.returnLabel)) {
                cfg.addVertex(i.label);
                cfg.addEdge(i, i.label);
            }
            // TODO: add support for arbitary jumps
        }

        // Jmp ends a block
        state = State.OUT_OF_BLOCK;
        return null;
    }

    public Instr visit(Label i) {
        if (state == State.IN_BLOCK) {
            // Remove fall-through edge to label
            cfg.removeEdge(prev, i);

            // Inject jump between two consecutive labels
            Jmp fallThrough = Jmp.toLabel(i);
            cfg.addVertex(fallThrough);
            cfg.addEdge(prev, fallThrough);
            cfg.addEdge(fallThrough, i);
        }

        // Label starts a new block
        state = State.IN_BLOCK;
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
        state = State.OUT_OF_BLOCK;
        return null;
    }

    public Instr visit(Setcc i) {
        return i;
    }

    public Instr visit(Text i) {
        return i;
    }

}