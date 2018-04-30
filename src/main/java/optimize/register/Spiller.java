package optimize.register;

import java.util.*;

import assemble.*;
import assemble.instructions.*;
import util.*;

public class Spiller extends InstrVisitor<List<Instr<Temp>>> {

    /**
     * Takes a list of instructions and spills all temps in the spilled set.
     */
    public List<Instr<Temp>> spillAll(List<Instr<Temp>> instructions) {

        List<Instr<Temp>> updated = new ArrayList<>(); 

        for (Instr<Temp> instr : instructions) {
            Pair<List<Instr<Temp>>, List<Instr<Temp>>> spills = instr.accept(this);

            if (spills != null) {
                if (spills.first != null) updated.addAll(spills.first);
                updated.add(instr);
                if (spills.second != null) updated.addAll(spills.second);
            } else {
                updated.add(instr);
            }
        }

        return updated;
    }

    private Set<Temp> spilled;
    private Map<Temp, Integer> location;
    private int offset;
    
    public Spiller(Set<Temp> spilled, int offset) {
        this.spilled = spilled;
        this.offset = offset;
        this.location = new HashMap<>();
    }

    private Mem<Temp> load(Temp t) {
        if (location.containsKey(t)) {
            return Mem.of(Temp.RBP, location.get(t));
        } else {
            Mem<Temp> mem = Mem.of(Temp.RBP, offset);
            location.put(t, offset);
            offset -= 8;
            return mem;
        }
    }

    private Map<Temp, Mem<Temp>> load(Mem<Temp> mem) {
        Map<Temp, Mem<Temp>> mems = new HashMap<>();

        switch (mem.kind) {
        case BRSO:
            if (spilled.contains(mem.base)) mems.put(mem.base, load(mem.base));
        default:
            if (spilled.contains(mem.reg)) mems.put(mem.reg, load(mem.reg));
        }

        return mems;
    }
    
    /*
     * BinOp Visitors
     */

    public List<Instr<Temp>> visit(BinOp.TIR b) {
        if (spilled.contains(b.dest)) {
            Temp t = TempFactory.generate();
            Mem<Temp> m = load(b.dest);
            return List.of(
                new Mov.TMR(m, t),
                new BinOp.TIR(b.kind, b.src, t),
                new Mov.TRM(t, m)
            );
        }
        return null;
    }

    public List<Instr<Temp>> visit(BinOp.TIM b) {
        Map<Temp, Mem<Temp>> mems = load(b.dest);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(mems.get(t), t));
        }
        
        return new Pair<>(before, after);
    }

    public List<Instr<Temp>> visit(BinOp.TRM b) {
        Map<Temp, Mem<Temp>> mems = load(b.dest);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(b.src)) {
            before.add(new Mov.TMR(load(b.src), b.src));
        }

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(mems.get(t), t));
        }

        return new Pair<>(before, after);
    }

    public List<Instr<Temp>> visit(BinOp.TMR b) {
        Map<Temp, Mem<Temp>> mems = load(b.src);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(b.dest)) {
            before.add(new Mov.TMR(load(b.dest), b.dest));
            after.add(new Mov.TRM(b.dest, load(b.dest)));
        }

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(mems.get(t), t));
        }

        return new Pair<>(before, after);
    }

    public List<Instr<Temp>> visit(BinOp.TRR b) {
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(b.src)) {
            before.add(new Mov.TMR(load(b.src), b.src));
        }
        
        if (spilled.contains(b.dest)) {
            before.add(new Mov.TMR(load(b.dest), b.dest));
            after.add(new Mov.TRM(b.dest, load(b.dest)));
        }

        return new Pair<>(before, after);
    }

    /*
     * Call Visitor
     */

    public List<Instr<Temp>> visit(Call.T c) {
        return null;
    }

    /*
     * Cmp Visitors
     */

    public List<Instr<Temp>> visit(Cmp.TIR c) {
        if (spilled.contains(c.right)) {
            return new Pair<>(
                List.of(new Mov.TMR(load(c.right), c.right)), 
                null
            );
        }
        return null;
    }

    public List<Instr<Temp>> visit(Cmp.TRM c) {
        Map<Temp, Mem<Temp>> mems = load(c.right);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(c.left)) {
            before.add(new Mov.TMR(load(c.left), c.left));
        }

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(mems.get(t), t));
        }

        return new Pair<>(before, after);
    }

    public List<Instr<Temp>> visit(Cmp.TMR c) {
        Map<Temp, Mem<Temp>> mems = load(c.left);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(c.right)) {
            before.add(new Mov.TMR(load(c.right), c.right));
            after.add(new Mov.TRM(c.right, load(c.right)));
        }

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(mems.get(t), t));
        }

        return new Pair<>(before, after);
    }

    public List<Instr<Temp>> visit(Cmp.TRR c) {
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(c.left)) {
            before.add(new Mov.TMR(load(c.left), c.left));
        }
        
        if (spilled.contains(c.right)) {
            before.add(new Mov.TMR(load(c.right), c.right));
        }

        return new Pair<>(before, after);
    }

    /*
     * Cqo Visitor
     */

    public List<Instr<Temp>> visit(Cqo.T c) {
        return null;
    }

    /*
     * DivMul Visitors
     */

    public List<Instr<Temp>> visit(DivMul.TR d) {
        if (spilled.contains(d.src)) {
            return new Pair<>(
                List.of(new Mov.TMR(load(d.src), d.src)),
                null
            );
        }

        return null;
    }

    public List<Instr<Temp>> visit(DivMul.TM d) {
        Map<Temp, Mem<Temp>> mems = load(d.src);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(mems.get(t), t));
        }

        return new Pair<>(before, after);
    }

    /*
     * Jcc Visitor
     */

    public List<Instr<Temp>> visit(Jcc.T j) {
        return null;
    }

    /*
     * Jmp Visitor
     */

    public List<Instr<Temp>> visit(Jmp.T j) {
        return null;
    }

    /*
     * Label Visitor
     */

    public List<Instr<Temp>> visit(Label.T l) {
        return null;
    }

    /*
     * Lea Visitor
     */

    public List<Instr<Temp>> visit(Lea.T l) {
        return null;
    }

    /*
     * Mov Visitors
     */

    public List<Instr<Temp>> visit(Mov.TIR m) {
        if (spilled.contains(m.dest)) {
            return new Pair<>(
                null,
                List.of(new Mov.TRM(m.dest, load(m.dest)))
            );
        }
        return null;
    }

    public List<Instr<Temp>> visit(Mov.TIM m) {
        return null;
    }

    public List<Instr<Temp>> visit(Mov.TRM m) {
        Map<Temp, Mem<Temp>> mems = load(m.dest);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(m.src)) {
            before.add(new Mov.TMR(load(m.src), m.src));
        }

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(mems.get(t), t));
        }

        return new Pair<>(before, after);
    }

    public List<Instr<Temp>> visit(Mov.TMR m) {
        Map<Temp, Mem<Temp>> mems = load(m.src);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(m.dest)) {
            before.add(new Mov.TMR(load(m.dest), m.dest));
            after.add(new Mov.TRM(m.dest, load(m.dest)));
        }

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(mems.get(t), t));
        }

        return new Pair<>(before, after);
    }

    public List<Instr<Temp>> visit(Mov.TRR m) {
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        if (spilled.contains(m.src)) {
            before.add(new Mov.TMR(load(m.src), m.src));
        }
        
        if (spilled.contains(m.dest)) {
            before.add(new Mov.TMR(load(m.dest), m.dest));
            after.add(new Mov.TRM(m.dest, load(m.dest)));
        }

        return new Pair<>(before, after);
    }
    
    /*
     * Pop Visitors
     */

    public List<Instr<Temp>> visit(Pop.TR p) {
        if (spilled.contains(p.dest)) {
            return new Pair<>(
                null,
                List.of(new Mov.TRM(p.dest, load(p.dest)))
            );
        }
        return null;
    }

    public List<Instr<Temp>> visit(Pop.TM p) {
        Map<Temp, Mem<Temp>> mems = load(p.dest);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(load(t), t));
        }

        return new Pair<>(before, after);
    }

    /*
     * Push Visitors
     */

    public List<Instr<Temp>> visit(Push.TR p) {
        if (spilled.contains(p.src)) {
            return new Pair<>(
                List.of(new Mov.TRM(p.src, load(p.src))),
                null
            );
        }
        return null;
    }

    public List<Instr<Temp>> visit(Push.TM p) {
        Map<Temp, Mem<Temp>> mems = load(p.src);
        List<Instr<Temp>> before = new ArrayList<>();
        List<Instr<Temp>> after = new ArrayList<>();

        for (Temp t : mems.keySet()) {
            before.add(new Mov.TMR(load(t), t));
        }

        return new Pair<>(before, after);
    }

    /*
     * Ret Visitor
     */

    public List<Instr<Temp>> visit(Ret.T r) {
        return null;
    }

    /*
     * Setcc Visitor
     */

    public List<Instr<Temp>> visit(Setcc.T s) {
        if (spilled.contains(s.dest)) {
            return new Pair<>(
                null,
                List.of(new Mov.TRM(s.dest, load(s.dest)))
            );
        }
        return null;
    }

    /*
     * List<Instr<Temp>>ext Visitor
     */

    public List<Instr<Temp>> visit(Text.T t) {
        return null;
    }
}
