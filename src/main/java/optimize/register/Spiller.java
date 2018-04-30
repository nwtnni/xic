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
            List<Instr<Temp>> expanded = instr.accept(this);
            updated.addAll(expanded);
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

    private Optional<Mem<Temp>> spill(Temp t) {
        if (!spilled.contains(t)) {
            return Optional.empty();
        } else if (location.containsKey(t)) {
            return Optional.of(Mem.of(Temp.RBP, location.get(t)));
        } else {
            Mem<Temp> mem = Mem.of(Temp.RBP, offset);
            location.put(t, offset);
            offset -= 8;
            return Optional.of(mem);
        }
    }

    private Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> spill(Mem<Temp> mem) {
        switch (mem.kind) {
        case BRSO: 
            Optional<Mem<Temp>> base = spill(mem.base);
            Optional<Mem<Temp>> reg = spill(mem.reg);

            Temp b = TempFactory.generate("SPILL_MEM_BASE");
            Temp r = TempFactory.generate("SPILL_MEM_REG");
            
            if (base.isPresent() && reg.isPresent()) {
                return Optional.of(new Pair<>(
                    Mem.of(b, r, mem.scale, mem.offset),
                    List.of(
                        new Mov.TMR(base.get(), b),
                        new Mov.TMR(reg.get(), r)
                    )
                ));
            } else if (base.isPresent()) {
                return Optional.of(new Pair<>(
                    Mem.of(b, mem.reg, mem.scale, mem.offset),
                    List.of(new Mov.TMR(base.get(), b))
                ));
            } else if (reg.isPresent()) {
                return Optional.of(new Pair<>(
                    Mem.of(mem.base, r, mem.scale, mem.offset),
                    List.of(new Mov.TMR(reg.get(), r))
                ));
            } else {
                return Optional.empty();
            }
        default:
            reg = spill(mem.reg); 

            if (!reg.isPresent()) return Optional.empty();

            r = TempFactory.generate("SPILL_MEM_REG");
            Mem<Temp> replaced = null;

            if (mem.kind == Mem.Kind.RSO) {
                replaced = Mem.of(r, mem.scale, mem.offset);
            } else if (mem.kind == Mem.Kind.RO) {
                replaced = Mem.of(r, mem.offset);
            } else {
                replaced = Mem.of(r);
            }

            return Optional.of(new Pair<>(
                replaced, List.of(new Mov.TMR(reg.get(), r))
            ));
        }
    }
    
    /*
     * BinOp Visitors
     */

    public List<Instr<Temp>> visit(BinOp.TIR b) {
        Optional<Mem<Temp>> m = spill(b.dest);

        if (!m.isPresent()) return List.of(b);

        Temp t = TempFactory.generate();
        return List.of(
            new Mov.TMR(m.get(), t),
            new BinOp.TIR(b.kind, b.src, t),
            new Mov.TRM(t, m.get())
        );
    }

    public List<Instr<Temp>> visit(BinOp.TIM b) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> ms = spill(b.dest);

        if (!ms.isPresent()) return List.of(b);

        Pair<Mem<Temp>, List<Instr<Temp>>> result = ms.get();
        List<Instr<Temp>> setup = new ArrayList<>(result.second);
        setup.add(new BinOp.TIM(b.kind, b.src, result.first));
        return setup;
    }

    public List<Instr<Temp>> visit(BinOp.TRM b) {
        Optional<Mem<Temp>> m = spill(b.src);
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> ms = spill(b.dest);

        if (!m.isPresent() && !ms.isPresent()) return List.of(b);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (m.isPresent() && ms.isPresent()) {
            Temp s = TempFactory.generate("SPILL_BINOP_RM_SRC");
            setup.add(new Mov.TMR(m.get(), s));
            setup.addAll(ms.get().second);
            setup.add(new BinOp.TRM(b.kind, s, ms.get().first));
        } else if (m.isPresent()) {
            Temp s = TempFactory.generate("SPILL_BINOP_RM_SRC");
            setup.add(new Mov.TMR(m.get(), s));
            setup.add(new BinOp.TRM(b.kind, s, b.dest));
        } else {
            setup.addAll(ms.get().second);
            setup.add(new BinOp.TRM(b.kind, b.src, ms.get().first));
        }

        return setup;
    }

    public List<Instr<Temp>> visit(BinOp.TMR b) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> ms = spill(b.src);
        Optional<Mem<Temp>> m = spill(b.dest);

        if (!m.isPresent() && !ms.isPresent()) return List.of(b);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (m.isPresent() && ms.isPresent()) {
            Temp s = TempFactory.generate("SPILL_BINOP_MR_DEST");
            setup.addAll(ms.get().second);
            setup.add(new Mov.TMR(m.get(), s));
            setup.add(new BinOp.TMR(b.kind, ms.get().first, s));
            setup.add(new Mov.TRM(s, m.get()));
        } else if (m.isPresent()) {
            Temp s = TempFactory.generate("SPILL_BINOP_MR_DEST");
            setup.add(new Mov.TMR(m.get(), s));
            setup.add(new BinOp.TMR(b.kind, b.src, s));
            setup.add(new Mov.TRM(s, m.get()));
        } else {
            setup.addAll(ms.get().second);
            setup.add(new BinOp.TMR(b.kind, ms.get().first, b.dest));
        }

        return setup;
    }

    public List<Instr<Temp>> visit(BinOp.TRR b) {
        Optional<Mem<Temp>> src = spill(b.src);
        Optional<Mem<Temp>> dest = spill(b.dest);

        if (!src.isPresent() && !dest.isPresent()) return List.of(b);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (src.isPresent() && dest.isPresent()) {
            Temp s = TempFactory.generate("SPILL_BINOP_RR_SRC");
            Temp d = TempFactory.generate("SPILL_BINOP_RR_DEST");
            setup.add(new Mov.TMR(src.get(), s));
            setup.add(new Mov.TMR(dest.get(), d));
            setup.add(new BinOp.TRR(b.kind, s, d));
            setup.add(new Mov.TRM(d, dest.get()));
        } else if (src.isPresent()) {
            Temp s = TempFactory.generate("SPILL_BINOP_RR_SRC");
            setup.add(new Mov.TMR(src.get(), s));
            setup.add(new BinOp.TRR(b.kind, s, b.dest));
        } else if (dest.isPresent()) {
            Temp d = TempFactory.generate("SPILL_BINOP_RR_DEST");
            setup.add(new Mov.TMR(dest.get(), d));
            setup.add(new BinOp.TRR(b.kind, b.src, d));
            setup.add(new Mov.TRM(d, dest.get()));
        }

        return setup;
    }

    /*
     * Call Visitor
     */

    public List<Instr<Temp>> visit(Call.T c) {
        return List.of(c);
    }

    /*
     * Cmp Visitors
     */

    public List<Instr<Temp>> visit(Cmp.TIR c) {
        Optional<Mem<Temp>> right = spill(c.right);
        
        if (!right.isPresent()) return List.of(c);

        Temp r = TempFactory.generate("SPILL_CMP_IR_RIGHT");
        return List.of(
            new Mov.TMR(right.get(), r),
            new Cmp.TIR(c.left, r)
        );
    }

    public List<Instr<Temp>> visit(Cmp.TRM c) {

        Optional<Mem<Temp>> m = spill(c.left);
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> ms = spill(c.right);

        if (!m.isPresent() && !ms.isPresent()) return List.of(c);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (m.isPresent() && ms.isPresent()) {
            Temp s = TempFactory.generate("SPILL_CMP_RM_SRC");
            setup.add(new Mov.TMR(m.get(), s));
            setup.addAll(ms.get().second);
            setup.add(new Cmp.TRM(s, ms.get().first));
        } else if (m.isPresent()) {
            Temp s = TempFactory.generate("SPILL_CMP_RM_SRC");
            setup.add(new Mov.TMR(m.get(), s));
            setup.add(new Cmp.TRM(s, c.right));
        } else {
            setup.addAll(ms.get().second);
            setup.add(new Cmp.TRM(c.left, ms.get().first));
        }

        return setup;
    }

    public List<Instr<Temp>> visit(Cmp.TMR c) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> ms = spill(c.left);
        Optional<Mem<Temp>> m = spill(c.right);

        if (!m.isPresent() && !ms.isPresent()) return List.of(c);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (m.isPresent() && ms.isPresent()) {
            Temp s = TempFactory.generate("SPILL_CMP_MR_DEST");
            setup.addAll(ms.get().second);
            setup.add(new Mov.TMR(m.get(), s));
            setup.add(new Cmp.TMR(ms.get().first, s));
        } else if (m.isPresent()) {
            Temp s = TempFactory.generate("SPILL_CMP_MR_DEST");
            setup.add(new Mov.TMR(m.get(), s));
            setup.add(new Cmp.TMR(c.left, s));
        } else {
            setup.addAll(ms.get().second);
            setup.add(new Cmp.TMR(ms.get().first, c.right));
        }

        return setup;
    }

    public List<Instr<Temp>> visit(Cmp.TRR c) {
        Optional<Mem<Temp>> left = spill(c.left);
        Optional<Mem<Temp>> right = spill(c.right);

        if (!left.isPresent() && !right.isPresent()) return List.of(c);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (left.isPresent() && right.isPresent()) {
            Temp s = TempFactory.generate("SPILL_CMP_RR_SRC");
            Temp d = TempFactory.generate("SPILL_CMP_RR_DEST");
            setup.add(new Mov.TMR(left.get(), s));
            setup.add(new Mov.TMR(right.get(), d));
            setup.add(new Cmp.TRR(s, d));
        } else if (left.isPresent()) {
            Temp s = TempFactory.generate("SPILL_CMP_RR_SRC");
            setup.add(new Mov.TMR(left.get(), s));
            setup.add(new Cmp.TRR(s, c.right));
        } else if (right.isPresent()) {
            Temp d = TempFactory.generate("SPILL_CMP_RR_DEST");
            setup.add(new Mov.TMR(right.get(), d));
            setup.add(new Cmp.TRR(c.left, d));
        }

        return setup;
    }

    /*
     * Cqo Visitor
     */

    public List<Instr<Temp>> visit(Cqo.T c) {
        return List.of(c);
    }

    /*
     * DivMul Visitors
     */

    public List<Instr<Temp>> visit(DivMul.TR d) {
        
        Optional<Mem<Temp>> src = spill(d.src);

        if (!src.isPresent()) return List.of(d);

        Temp s = TempFactory.generate("SPILL_DIVMUL_SRC");
        return List.of(
            new Mov.TMR(src.get(), s),
            new DivMul.TR(d.kind, s)
        );
    }

    public List<Instr<Temp>> visit(DivMul.TM d) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> src = spill(d.src);

        if (!src.isPresent()) return List.of(d);
        
        List<Instr<Temp>> setup = new ArrayList<>(src.get().second);
        setup.add(new DivMul.TM(d.kind, src.get().first));
        return setup;
    }

    /*
     * Jcc Visitor
     */

    public List<Instr<Temp>> visit(Jcc.T j) {
        return List.of(j);
    }

    /*
     * Jmp Visitor
     */

    public List<Instr<Temp>> visit(Jmp.T j) {
        return List.of(j);
    }

    /*
     * Label Visitor
     */

    public List<Instr<Temp>> visit(Label.T l) {
        return List.of(l);
    }

    /*
     * Lea Visitor
     */

    public List<Instr<Temp>> visit(Lea.T l) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> ms = spill(l.src);
        Optional<Mem<Temp>> m = spill(l.dest);

        if (!m.isPresent() && !ms.isPresent()) return List.of(l);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (m.isPresent() && ms.isPresent()) {
            Temp s = TempFactory.generate("SPILL_LEA_DEST");
            setup.addAll(ms.get().second);
            setup.add(new Mov.TMR(m.get(), s));
            setup.add(new Lea.T(ms.get().first, s));
            setup.add(new Mov.TRM(s, m.get()));
        } else if (m.isPresent()) {
            Temp s = TempFactory.generate("SPILL_LEA_DEST");
            setup.add(new Mov.TMR(m.get(), s));
            setup.add(new Lea.T(l.src, s));
            setup.add(new Mov.TRM(s, m.get()));
        } else {
            setup.addAll(ms.get().second);
            setup.add(new Lea.T(ms.get().first, l.dest));
        }

        return setup;
    }

    /*
     * Mov Visitors
     */

    public List<Instr<Temp>> visit(Mov.TIR m) {
        Optional<Mem<Temp>> dest = spill(m.dest);

        if (!dest.isPresent()) return List.of(m);

        Temp t = TempFactory.generate();
        return List.of(
            new Mov.TMR(dest.get(), t),
            new Mov.TIR(m.src, t),
            new Mov.TRM(t, dest.get())
        );
    }

    public List<Instr<Temp>> visit(Mov.TIM m) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> dest = spill(m.dest);

        if (!dest.isPresent()) return List.of(m);

        Pair<Mem<Temp>, List<Instr<Temp>>> result = dest.get();
        List<Instr<Temp>> setup = new ArrayList<>(result.second);
        setup.add(new Mov.TIM(m.src, result.first));
        return setup;
    }

    public List<Instr<Temp>> visit(Mov.TRM m) {
        Optional<Mem<Temp>> src = spill(m.src);
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> dest = spill(m.dest);

        if (!src.isPresent() && !dest.isPresent()) return List.of(m);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (src.isPresent() && dest.isPresent()) {
            Temp s = TempFactory.generate("SPILL_MOV_RM_SRC");
            setup.add(new Mov.TMR(src.get(), s));
            setup.addAll(dest.get().second);
            setup.add(new Mov.TRM(s, dest.get().first));
        } else if (src.isPresent()) {
            Temp s = TempFactory.generate("SPILL_MOV_RM_SRC");
            setup.add(new Mov.TMR(src.get(), s));
            setup.add(new Mov.TRM(s, m.dest));
        } else {
            setup.addAll(dest.get().second);
            setup.add(new Mov.TRM(m.src, dest.get().first));
        }

        return setup;
    }

    public List<Instr<Temp>> visit(Mov.TMR m) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> src = spill(m.src);
        Optional<Mem<Temp>> dest = spill(m.dest);

        if (!src.isPresent() && !dest.isPresent()) return List.of(m);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (src.isPresent() && dest.isPresent()) {
            Temp s = TempFactory.generate("SPILL_MOV_MR_DEST");
            setup.addAll(src.get().second);
            setup.add(new Mov.TMR(src.get().first, s));
            setup.add(new Mov.TRM(s, dest.get()));
        } else if (dest.isPresent()) {
            Temp s = TempFactory.generate("SPILL_MOV_MR_DEST");
            setup.add(new Mov.TMR(m.src, s));
            setup.add(new Mov.TRM(s, dest.get()));
        } else {
            setup.addAll(src.get().second);
            setup.add(new Mov.TMR(src.get().first, m.dest));
        }

        return setup;
    }

    public List<Instr<Temp>> visit(Mov.TRR m) {
        Optional<Mem<Temp>> src = spill(m.src);
        Optional<Mem<Temp>> dest = spill(m.dest);

        if (!src.isPresent() && !dest.isPresent()) return List.of(m);

        List<Instr<Temp>> setup = new ArrayList<>();

        if (src.isPresent() && dest.isPresent()) {
            Temp s = TempFactory.generate("SPILL_MOV_RR_SRC");
            Temp d = TempFactory.generate("SPILL_MOV_RR_DEST");
            setup.add(new Mov.TMR(src.get(), s));
            setup.add(new Mov.TRR(s, d));
            setup.add(new Mov.TRM(d, dest.get()));
        } else if (src.isPresent()) {
            Temp s = TempFactory.generate("SPILL_MOV_RR_SRC");
            setup.add(new Mov.TMR(src.get(), s));
            setup.add(new Mov.TRR(s, m.dest));
        } else if (dest.isPresent()) {
            Temp d = TempFactory.generate("SPILL_MOV_RR_DEST");
            setup.add(new Mov.TRR(m.src, d));
            setup.add(new Mov.TRM(d, dest.get()));
        }

        return setup;
    }
    
    /*
     * Pop Visitors
     */

    public List<Instr<Temp>> visit(Pop.TR p) {
        
        Optional<Mem<Temp>> dest = spill(p.dest);

        if (!dest.isPresent()) return List.of(p);

        Temp s = TempFactory.generate("SPILL_POP_DEST");
        return List.of(
            new Pop.TR(s),
            new Mov.TRM(s, dest.get())
        );
    }

    public List<Instr<Temp>> visit(Pop.TM p) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> dest = spill(p.dest);

        if (!dest.isPresent()) return List.of(p);
        
        List<Instr<Temp>> setup = new ArrayList<>(dest.get().second);
        setup.add(new Pop.TM(dest.get().first));
        return setup;
    }

    /*
     * Push Visitors
     */

    public List<Instr<Temp>> visit(Push.TR p) {
        
        Optional<Mem<Temp>> src = spill(p.src);

        if (!src.isPresent()) return List.of(p);

        Temp s = TempFactory.generate("SPILL_PUSH_SRC");
        return List.of(
            new Mov.TMR(src.get(), s),
            new Push.TR(s)
        );
    }

    public List<Instr<Temp>> visit(Push.TM p) {
        Optional<Pair<Mem<Temp>, List<Instr<Temp>>>> src = spill(p.src);

        if (!src.isPresent()) return List.of(p);
        
        List<Instr<Temp>> setup = new ArrayList<>(src.get().second);
        setup.add(new Push.TM(src.get().first));
        return setup;
    }

    /*
     * Ret Visitor
     */

    public List<Instr<Temp>> visit(Ret.T r) {
        return List.of(r);
    }

    /*
     * Setcc Visitor
     */

    public List<Instr<Temp>> visit(Setcc.T s) {
        return List.of(s);
    }

    /*
     * List<Instr<Temp>>ext Visitor
     */

    public List<Instr<Temp>> visit(Text.T t) {
        return List.of(t);
    }
}
