package optimize.graph;

import java.util.HashMap;
import java.util.Map;

import assemble.*;
import assemble.instructions.*;
import emit.IRLabelFactory;

/** Factory class for generating ASAGraphs from IR. */
public class ASAGraphFactory<E> extends InstrVisitor<Instr<Temp>> {

    /** Construct a graph factory with edges specified by the edge factory. */
    public ASAGraphFactory(ASAEdgeFactory<E> edgeFactory) {
        this.edgeFactory = edgeFactory;
    }

    private enum State { IN_BLOCK, OUT_OF_BLOCK; }

    /** 
     * State is used during traversal to decide whether to inject jumps 
     * are needed to create extended basic block in the case of 
     * consecutive labels with no separating jump.
     */
    private State state;

    /** The edge factory used to contruct edges in the CFG */
    private ASAEdgeFactory<E> edgeFactory;

    /** Current CFG being constructed. */
    private ASAGraph<E> cfg;

    /** Previous statement in the assembly. */
    private Instr<Temp> prev;

    /** 
     * Returns the map of function names to their corresponding CFGs 
     * for the compilation unit. 
     */
    public Map<String, ASAGraph<E>> getAllCfgs(CompUnit<Temp> compUnit) {
        Map<String, ASAGraph<E>> fns = new HashMap<>();
        for (FuncDecl<Temp> fn : compUnit.fns) {
            // Generate graph
            makeCfg(fn);
            fns.put(fn.name, cfg);
        }
        return fns;
    }

    /** Returns the CFG for a single function. */
    public ASAGraph<E> makeCfg(FuncDecl<Temp> fn) {
        state = State.IN_BLOCK;

        Label<Temp> start = new Label.T(IRLabelFactory.generate("START"));
        prev = start;
        cfg = new ASAGraph<>(fn, start, edgeFactory);

        for (Instr<Temp> ins : fn.stmts) {
            // Add edge if in a block and not the first node
            if (state == State.IN_BLOCK && prev != null) {
                cfg.addVertex(ins);
                cfg.addEdge(prev, ins);
            }

            prev = ins.accept(this);
        }
        
        return cfg;
    }

    /*
     * BinOp Visitors
     */

    public Instr<Temp> visit(BinOp.TIR b) {
        return b;
    }

    public Instr<Temp> visit(BinOp.TIM b) {
        return b;
    }

    public Instr<Temp> visit(BinOp.TRM b) {
        return b;
    }

    public Instr<Temp> visit(BinOp.TMR b) {
        return b;
    }

    public Instr<Temp> visit(BinOp.TRR b) {
        return b;
    }

    public Instr<Temp> visit(Call<Temp> i) {
        return i;
    }

    /*
     * Call Visitor
     */

    public Instr<Temp> visit(Call.T c) {
        return c;
    }

    /*
     * Cmp Visitors
     */

    public Instr<Temp> visit(Cmp.TIR c) {
        return c;
    }

    public Instr<Temp> visit(Cmp.TRM c) {
        return c;
    }

    public Instr<Temp> visit(Cmp.TMR c) {
        return c;
    }

    public Instr<Temp> visit(Cmp.TRR c) {
        return c;
    }

    /*
     * Cqo Visitor
     */

    public Instr<Temp> visit(Cqo.T c) {
        return c;
    }

    /*
     * DivMul Visitors
     */

    public Instr<Temp> visit(DivMul.TR d) {
        return d;
    }

    public Instr<Temp> visit(DivMul.TM d) {
        return d;
    }

    /*
     * Jcc Visitor
     */

    public Instr<Temp> visit(Jcc.T j) {
        if (state == State.IN_BLOCK) {
            // Add edge to target
            cfg.addVertex(j.target);
            cfg.addEdge(j, j.target);
        }

        // Jcc falls through so no state change
        return j;
    }

    /*
     * Jmp Visitor
     */

    public Instr<Temp> visit(Jmp.T j) {
        if (state == State.IN_BLOCK) {
            // Add edge to jump target if not the return label
            if (j.hasLabel() && !j.label.equals(cfg.originalFn.returnLabel)) {
                cfg.addVertex(j.label);
                cfg.addEdge(j, j.label);
            }
            // TODO: add support for arbitary jumps
        }

        // Jmp ends a block
        state = State.OUT_OF_BLOCK;
        return null;
    }

    /*
     * Label Visitor
     */

    public Instr<Temp> visit(Label.T l) {
        if (state == State.IN_BLOCK) {
            // Remove fall-through edge to label
            cfg.removeEdge(prev, l);

            // Inject jump between two consecutive labels
        Jmp<Temp> fallThrough = new Jmp.T(l);
            cfg.addVertex(fallThrough);
            cfg.addEdge(prev, fallThrough);
            cfg.addEdge(fallThrough, l);
        }

        // Label starts a new block
        state = State.IN_BLOCK;
        return l;
    }

    /*
     * Lea Visitor
     */

    public Instr<Temp> visit(Lea.T l) {
        return l;
    }

    /*
     * Mov Visitors
     */

    public Instr<Temp> visit(Mov.TIR m) {
        return m;
    }

    public Instr<Temp> visit(Mov.TIM m) {
        return m;
    }

    public Instr<Temp> visit(Mov.TRM m) {
        return m;
    }

    public Instr<Temp> visit(Mov.TMR m) {
        return m;
    }

    public Instr<Temp> visit(Mov.TRR m) {
        return m;
    }
    
    /*
     * Pop Visitors
     */

    public Instr<Temp> visit(Pop.TR p) {
        return p;
    }

    public Instr<Temp> visit(Pop.TM p) {
        return p;
    }

    /*
     * Push Visitors
     */

    public Instr<Temp> visit(Push.TR p) {
        return p;
    }

    public Instr<Temp> visit(Push.TM p) {
        return p;
    }

    /*
     * Ret Visitor
     */

    public Instr<Temp> visit(Ret.T r) {
        return r;
    }

    /*
     * Setcc Visitor
     */

    public Instr<Temp> visit(Setcc.T s) {
        return s;
    }

    /*
     * Text Visitor
     */

    public Instr<Temp> visit(Text.T t) {
        return t;
    }
}
