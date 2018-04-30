package optimize.register;

import java.util.*;

import assemble.*;
import assemble.instructions.*;

public class TempReplacer extends InstrVisitor<Set<Temp>> {

    /**
     * Coalesces temporaries in a FuncDecl fn given the analysis in ColorGraph cg.
     * 
     * Returns the set of of callee saved registers that have been coalesced and
     * are no longer available for coloring.
     */
    public static Set<Reg> replaceAll(FuncDecl<Temp> fn, ColorGraph cg) {
        TempReplacer t = new TempReplacer(cg);
        List<Instr<Temp>> instrs = new ArrayList<>();
        Set<Reg> calleeCoalesced = new HashSet<>();

        for (int i = 0; i < fn.stmts.size(); i++) {
            Instr<Temp> ins = fn.stmts.get(i);
            Set<Temp> replaced = t.replace(ins);
            if (replaced.isEmpty()) {
                instrs.add(ins);

            // For moves
            } else {
                // check for callee
                Temp temp = replaced.stream().findAny().get();
                if (temp.isFixed() && temp.getRegister().isCalleeSaved()) {
                    calleeCoalesced.add(temp.getRegister());
                }
            }
        }

        System.out.println(calleeCoalesced);

        fn.stmts = instrs;
        return calleeCoalesced;
    }

    private ColorGraph cg;

    private final static Set<Temp> EMPTY = Set.of();

    private TempReplacer(ColorGraph cg) {
        this.cg = cg;
    }

    /**
     * Coalesces temps inside the given instruction.
     *
     * Returns true if this instruction must be kept in the list.
     * Returns false if this instruction can be deleted as a result of coalescing.
     */
    private Set<Temp> replace(Instr<Temp> instr) {
        return instr.accept(this);
    }

    /**
     * Replaces [from] Temps inside [mem] with [to].
     */
    private void replace(Mem<Temp> mem) {
        switch (mem.kind) {
        case BRSO:
            mem.base = cg.getAlias(mem.base);
        default:
            mem.reg = cg.getAlias(mem.reg);
        }
    }

    /*
     * BinOp Visitors
     */

    @Override
    public Set<Temp> visit(BinOp.TIR b) {
        b.dest = cg.getAlias(b.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(BinOp.TIM b) {
        replace(b.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(BinOp.TRM b) {
        b.src = cg.getAlias(b.src);
        replace(b.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(BinOp.TMR b) {
        replace(b.src);
        b.dest = cg.getAlias(b.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(BinOp.TRR b) {
        b.src = cg.getAlias(b.src);
        b.dest = cg.getAlias(b.dest);
        return EMPTY;
    }

    /*
     * Call Visitor
     */

    @Override
    public Set<Temp> visit(Call.T c) {
        return EMPTY;
    }

    /*
     * Cmp Visitors
     */

    @Override
    public Set<Temp> visit(Cmp.TIR c) {
        c.right = cg.getAlias(c.right);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Cmp.TRM c) {
        c.left = cg.getAlias(c.left);
        replace(c.right);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Cmp.TMR c) {
        replace(c.left);
        c.right = cg.getAlias(c.right);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Cmp.TRR c) {
        c.left = cg.getAlias(c.left);
        c.right = cg.getAlias(c.right);
        return EMPTY;
    }

    /*
     * Cqo Visitor
     */

    @Override
    public Set<Temp> visit(Cqo.T c) {
        return EMPTY;
    }

    /*
     * DivMul Visitors
     */

    @Override
    public Set<Temp> visit(DivMul.TR d) {
        d.src = cg.getAlias(d.src);
        if (!cg.getAlias(d.dest).equals(d.dest)) {
            // Error in register allocation
            // Can't alias fixed register dest
            assert false;
        }
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(DivMul.TM d) {
        replace(d.src);
        if (!cg.getAlias(d.dest).equals(d.dest)) {
            // Error in register allocation
            // Can't alias fixed register dest
            assert false;
        }
        return EMPTY;
    }

    /*
     * Jcc Visitor
     */

    @Override
    public Set<Temp> visit(Jcc.T j) {
        return EMPTY;
    }

    /*
     * Jmp Visitor
     */

    @Override
    public Set<Temp> visit(Jmp.T j) {
        return EMPTY;
    }

    /*
     * Label Visitor
     */

    @Override
    public Set<Temp> visit(Label.T l) {
        return EMPTY;
    }

    /*
     * Lea Visitor
     */

    @Override
    public Set<Temp> visit(Lea.T l) {
        replace(l.src);
        l.dest = cg.getAlias(l.dest);
        return EMPTY;
    }

    /*
     * Mov Visitors
     */

    @Override
    public Set<Temp> visit(Mov.TIR m) {
        m.dest = cg.getAlias(m.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Mov.TIM m) {
        replace(m.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Mov.TRM m) {
        m.src = cg.getAlias(m.src);
        replace(m.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Mov.TMR m) {
        replace(m.src);
        m.dest = cg.getAlias(m.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Mov.TRR m) {
        m.src = cg.getAlias(m.src);
        m.dest = cg.getAlias(m.dest);

        if (m.src.equals(m.dest)) {
            return Set.of(m.src);
        } else {
            return EMPTY;
        }
    }
    
    /*
     * Pop Visitors
     */

    @Override
    public Set<Temp> visit(Pop.TR p) {
        p.dest = cg.getAlias(p.dest);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Pop.TM p) {
        replace(p.dest);
        return EMPTY;
    }

    /*
     * Push Visitors
     */

    @Override
    public Set<Temp> visit(Push.TR p) {
        p.src = cg.getAlias(p.src);
        return EMPTY;
    }

    @Override
    public Set<Temp> visit(Push.TM p) {
        replace(p.src);
        return EMPTY;
    }

    /*
     * Ret Visitor
     */

    @Override
    public Set<Temp> visit(Ret.T r) {
        return EMPTY;
    }

    /*
     * Setcc Visitor
     */

    @Override
    public Set<Temp> visit(Setcc.T s) {
        if (!cg.getAlias(s.dest).equals(s.dest)) {
            // Error in register allocation
            // Fixed register can't be alias
            assert false;
        }
        return EMPTY;
    }

    /*
     * Set<Temp>ext Visitor
     */

    @Override
    public Set<Temp> visit(Text.T t) {
        return EMPTY;
    }
}
