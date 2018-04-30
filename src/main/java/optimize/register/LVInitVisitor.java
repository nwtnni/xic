package optimize.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import assemble.*;
import assemble.instructions.*;
import optimize.graph.ASAGraph;
import util.Pair;

public class LVInitVisitor extends InstrVisitor<Void> {

    /**
     * Returns a pair of mappings of instructions to use/def sets for each 
     * instruction in the given set.
     */
    public static Pair<Map<Instr<Temp>, Set<Temp>>, Map<Instr<Temp>, Set<Temp>>> init(ASAGraph<Set<Temp>> cfg) {
        LVInitVisitor visitor = new LVInitVisitor(cfg);
        for (Instr<Temp> ins : cfg.vertexSet()) {
            ins.accept(visitor);
        }

        return new Pair<>(visitor.use, visitor.def);
    }

    private LVInitVisitor(ASAGraph<Set<Temp>> cfg) {
        this.cfg = cfg;
        this.use = new HashMap<>();
        this.def = new HashMap<>();
    }

    /** The cfg to initialize live variables on. */
    ASAGraph<Set<Temp>> cfg;

    /** Set of uses at each program point. */
    private Map<Instr<Temp>, Set<Temp>> use;
    
    /** Set of defs at each program point. */
    private Map<Instr<Temp>, Set<Temp>> def;

    /** Empty set constant. */
    private static final Set<Temp> EMPTY = Set.of();

    /** Utility method to update use/def sets. */
    private void update(Instr<Temp> ins, Set<Temp> u, Set<Temp> d) {
        use.put(ins, u);
        def.put(ins, d);
    }

    /*
     * BinOp Visitors
     */

    public Void visit(BinOp.TIR b) {
        Set<Temp> t = Set.of(b.dest);
        update(b, t, t);
        return null;
    }

    public Void visit(BinOp.TIM b) {
        Set<Temp> t = Mem.getTemps(b.dest);
        update(b, t, EMPTY);
        return null;
    }

    public Void visit(BinOp.TRM b) {
        Set<Temp> u = Mem.getTemps(b.dest);
        u.add(b.src);
        update(b, u, EMPTY);
        return null;
    }

    public Void visit(BinOp.TMR b) {
        Set<Temp> u = Mem.getTemps(b.src);
        u.add(b.dest);
        update(b, u, Set.of(b.dest));
        return null;
    }

    public Void visit(BinOp.TRR b) {
        Set<Temp> t = Set.of(b.src, b.dest);
        update(b, t, Set.of(b.dest));
        return null;
    }

    /*
     * Call Visitor
     */

    public Void visit(Call.T c) {
        update(c, 
            EMPTY,
            Set.of(Temp.RAX, Temp.RCX, Temp.RDX, 
            Temp.RSI, Temp.RDI, Temp.R8, 
            Temp.R9, Temp.R10, Temp.R11)
        );
        return null;
    }

    /*
     * Cmp Visitors
     */

    public Void visit(Cmp.TIR c) {
        update(c, Set.of(c.right), EMPTY);
        return null;
    }

    public Void visit(Cmp.TRM c) {
        Set<Temp> u = Mem.getTemps(c.right);
        u.add(c.left);
        update(c, u, EMPTY);
        return null;
    }

    public Void visit(Cmp.TMR c) {
        Set<Temp> u = Mem.getTemps(c.left);
        u.add(c.right);
        update(c, u, EMPTY);
        return null;
    }

    public Void visit(Cmp.TRR c) {
        update(c, Set.of(c.left, c.right), EMPTY);
        return null;
    }

    /*
     * Cqo Visitor
     */

    public Void visit(Cqo.T c) {
        update(c, Set.of(Temp.RAX), Set.of(Temp.RDX));
        return null;
    }

    /*
     * DivMul Visitors
     */

    public Void visit(DivMul.TR d) {
        Set<Temp> u = new HashSet<>(Set.of(d.src, Temp.RAX));
        if (d.usesRDX()) {
            u.add(Temp.RDX);
        }
        update(d, u, Set.of(Temp.RAX, Temp.RDX));
        return null;
    }

    public Void visit(DivMul.TM d) {
        Set<Temp> u = Mem.getTemps(d.src);
        u.add(Temp.RAX);
        if (d.usesRDX()) {
            u.add(Temp.RDX);
        }
        update(d, u, Set.of(Temp.RAX, Temp.RDX));
        return null;
    }

    /*
     * Jcc Visitor
     */

    public Void visit(Jcc.T j) {
        update(j, EMPTY, EMPTY);
        return null;
    }

    /*
     * Jmp Visitor
     */

    public Void visit(Jmp.T j) {
        // Add %rax and %rdx to use set if jump to return
        Set<Temp> use = new HashSet<>(Set.of(Temp.RBX, Temp.R12, Temp.R13, Temp.R14, Temp.R15));
        if (j.label.equals(cfg.originalFn.returnLabel)) {
            if (cfg.originalFn.rets > 0)
                use.add(Temp.RAX);
            if (cfg.originalFn.rets > 1)
                use.add(Temp.RDX);
        }
        update(j, use, EMPTY);
        return null;
    }

    /*
     * Label Visitor
     */

    public Void visit(Label.T l) {
        update(l, EMPTY, EMPTY);
        return null;
    }

    /*
     * Lea Visitor
     */

    public Void visit(Lea.T l) {
        update(l, Mem.getTemps(l.src), Set.of(l.dest));
        return null;
    }

    /*
     * Mov Visitors
     */

    public Void visit(Mov.TIR m) {
        update(m, EMPTY, Set.of(m.dest));
        return null;
    }

    public Void visit(Mov.TIM m) {
        update(m, Mem.getTemps(m.dest), EMPTY);
        return null;
    }

    public Void visit(Mov.TRM m) {
        Set<Temp> u = Mem.getTemps(m.dest);
        u.add(m.src);
        update(m, u, EMPTY);
        return null;
    }

    public Void visit(Mov.TMR m) {
        Set<Temp> u = Mem.getTemps(m.src);
        update(m, u, Set.of(m.dest));
        return null;
    }

    public Void visit(Mov.TRR m) {
        update(m, Set.of(m.src), Set.of(m.dest));
        return null;
    }
    
    /*
     * Pop Visitors
     */

    public Void visit(Pop.TR p) {
        update(p, EMPTY, Set.of(p.dest));
        return null;
    }

    public Void visit(Pop.TM p) {
        update(p, Mem.getTemps(p.dest), EMPTY);
        return null;
    }

    /*
     * Push Visitors
     */

    public Void visit(Push.TR p) {
        update(p, Set.of(p.src), EMPTY);
        return null;
    }

    public Void visit(Push.TM p) {
        update(p, Mem.getTemps(p.src), EMPTY);
        return null;
    }

    /*
     * Ret Visitor
     */

    public Void visit(Ret.T r) {
        update(r, EMPTY, EMPTY);
        return null;
    }

    /*
     * Setcc Visitor
     */

    public Void visit(Setcc.T s) {
        update(s, EMPTY, Set.of(Temp.RAX));
        return null;
    }

    public Void visit(Text.T t) {
        update(t, EMPTY, EMPTY);
        return null;
    }

}
