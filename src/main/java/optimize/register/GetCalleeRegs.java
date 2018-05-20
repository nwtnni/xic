package optimize.register;

import java.util.*;

import assemble.*;
import assemble.instructions.*;

/** 
 * Utility visitor for extracting the set of callee registers that have been 
 * coalesced and are no longer available for later iterations of coloring.
 */
public class GetCalleeRegs extends InstrVisitor<Set<Reg>> {

    /**
     * Coalesces temporaries in a FuncDecl fn given the analysis in ColorGraph cg.
     * 
     * Returns the set of of callee saved registers that have been coalesced and
     * are no longer available for coloring.
     */
    public static Set<Reg> getCalleeRegs(FuncDecl<Temp> fn) {
        GetCalleeRegs gcr = new GetCalleeRegs();
        Set<Reg> calleeRegs = new HashSet<>();


        for (int i = 0; i < fn.stmts.size(); i++) {
            Instr<Temp> ins = fn.stmts.get(i);
            calleeRegs.addAll(gcr.getCalleeRegs(ins));
        }

        calleeRegs.remove(Reg.RSP);
        calleeRegs.remove(Reg.RBP);
        return calleeRegs;
    }

    private final static Set<Reg> EMPTY = Set.of();

    private Set<Reg> wrap(Temp t) {
        if (t.isFixed() && t.getRegister().isCalleeSaved()) {
            return Set.of(t.getRegister());
        }
        else {
            return Set.of();
        }
    }

    private <T> Set<T> union(Set<T> s1, Set<T> s2) {
        Set<T> union = new HashSet<>(s1);
        union.addAll(s2);
        return union;
    }

    /**
     * Coalesces temps inside the given instruction.
     *
     * Returns true if this instruction must be kept in the list.
     * Returns false if this instruction can be deleted as a result of coalescing.
     */
    private Set<Reg> getCalleeRegs(Instr<Temp> instr) {
        return instr.accept(this);
    }

    // TODO: this is sketchy
    /**
     * Gets temps inside [mem].
     */
    private Set<Reg> checkMem(Mem<Temp> mem) {
        switch (mem.kind) {
        case BRSO:
            return union(wrap(mem.base), wrap(mem.reg));
        default:
            return wrap(mem.reg);
        }
    }

    /*
     * BinOp Visitors
     */

    @Override
    public Set<Reg> visit(BinOp.TIR b) {
        return wrap(b.dest);
    }

    @Override
    public Set<Reg> visit(BinOp.TIM b) {
        return checkMem(b.dest);
    }

    @Override
    public Set<Reg> visit(BinOp.TRM b) {
        return union(wrap(b.src), checkMem(b.dest));
    }

    @Override
    public Set<Reg> visit(BinOp.TMR b) {
        return union(checkMem(b.src), wrap(b.dest));
    }

    @Override
    public Set<Reg> visit(BinOp.TRR b) {
        return union(wrap(b.src), wrap(b.dest));
    }

    /*
     * Call Visitor
     */

    @Override
    public Set<Reg> visit(Call.TL c) {
        return EMPTY;
    }

    @Override
    public Set<Reg> visit(Call.TR c) {
        return wrap(c.name);
    }

    @Override
    public Set<Reg> visit(Call.TM c) {
        return checkMem(c.name);
    }

    /*
     * Cmp Visitors
     */

    @Override
    public Set<Reg> visit(Cmp.TIR c) {
        return wrap(c.right);
    }

    @Override
    public Set<Reg> visit(Cmp.TRM c) {
        return union(wrap(c.left), checkMem(c.right));
    }

    @Override
    public Set<Reg> visit(Cmp.TMR c) {
        return union(checkMem(c.left), wrap(c.right));
    }

    @Override
    public Set<Reg> visit(Cmp.TRR c) {
        return union(wrap(c.left), wrap(c.right));
    }

    /*
     * Cqo Visitor
     */

    @Override
    public Set<Reg> visit(Cqo.T c) {
        return EMPTY;
    }

    /*
     * DivMul Visitors
     */

    @Override
    public Set<Reg> visit(DivMul.TR d) {
        return wrap(d.src);
    }

    @Override
    public Set<Reg> visit(DivMul.TM d) {
        return checkMem(d.src);
    }

    /*
     * Jcc Visitor
     */

    @Override
    public Set<Reg> visit(Jcc.T j) {
        return EMPTY;
    }

    /*
     * Jmp Visitor
     */

    @Override
    public Set<Reg> visit(Jmp.T j) {
        return EMPTY;
    }

    /*
     * Label Visitor
     */

    @Override
    public Set<Reg> visit(Label.T l) {
        return EMPTY;
    }

    /*
     * Lea Visitor
     */

    @Override
    public Set<Reg> visit(Lea.T l) {
        return union(checkMem(l.src), wrap(l.dest));
    }

    /*
     * Mov Visitors
     */

    @Override
    public Set<Reg> visit(Mov.TIR m) {
        return wrap(m.dest);
    }

    @Override
    public Set<Reg> visit(Mov.TIM m) {
        return checkMem(m.dest);
    }

    @Override
    public Set<Reg> visit(Mov.TRM m) {
        return union(wrap(m.src), checkMem(m.dest));
    }

    @Override
    public Set<Reg> visit(Mov.TMR m) {
        return union(checkMem(m.src), wrap(m.dest));
    }

    @Override
    public Set<Reg> visit(Mov.TRR m) {
        return union(wrap(m.src), wrap(m.dest));
    }
    
    @Override
    public Set<Reg> visit(Mov.TLR m) {
        return wrap(m.dest);
    }

    @Override
    public Set<Reg> visit(Mov.TRL m) {
        return wrap(m.src);
    }

    /*
     * Pop Visitors
     */

    @Override
    public Set<Reg> visit(Pop.TR p) {
        return wrap(p.dest);
    }

    @Override
    public Set<Reg> visit(Pop.TM p) {
        return checkMem(p.dest);
    }

    /*
     * Push Visitors
     */

    @Override
    public Set<Reg> visit(Push.TR p) {
        return wrap(p.src);
    }

    @Override
    public Set<Reg> visit(Push.TM p) {
        return checkMem(p.src);
    }

    /*
     * Ret Visitor
     */

    @Override
    public Set<Reg> visit(Ret.T r) {
        return EMPTY;
    }

    /*
     * Setcc Visitor
     */

    @Override
    public Set<Reg> visit(Setcc.T s) {
        return EMPTY;
    }

    /*
     * Set<Reg>ext Visitor
     */

    @Override
    public Set<Reg> visit(Text.T t) {
        return EMPTY;
    }
}
