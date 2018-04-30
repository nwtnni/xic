package optimize.register;

import java.util.*;

import assemble.*;
import assemble.instructions.*;
import util.*;

public class Spiller extends InstrVisitor<Pair<List<Instr<Temp>>, List<Instr<Temp>>>> {

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

    public Spiller(Set<Temp> spilled) {
        this.spilled = spilled;
    }
    
    /*
     * BinOp Visitors
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(BinOp.TIR b) {

    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(BinOp.TIM b) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(BinOp.TRM b) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(BinOp.TMR b) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(BinOp.TRR b) {
        return null;
    }

    /*
     * Call Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Call.T c) {
        return null;
    }

    /*
     * Cmp Visitors
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Cmp.TIR c) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Cmp.TRM c) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Cmp.TMR c) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Cmp.TRR c) {
        return null;
    }

    /*
     * Cqo Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Cqo.T c) {
        return null;
    }

    /*
     * DivMul Visitors
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(DivMul.TR d) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(DivMul.TM d) {
        return null;
    }

    /*
     * Jcc Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Jcc.T j) {
        return null;
    }

    /*
     * Jmp Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Jmp.T j) {
        return null;
    }

    /*
     * Label Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Label.T l) {
        return null;
    }

    /*
     * Lea Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Lea.T l) {
        return null;
    }

    /*
     * Mov Visitors
     */

    public <L, R> Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Mov.TIR m) {
        return null;
    }

    public <L, R> Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Mov.TIM m) {
        return null;
    }

    public <L, R> Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Mov.TRM m) {
        return null;
    }

    public <L, R> Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Mov.TMR m) {
        return null;
    }

    public <L, R> Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Mov.TRR m) {
        return null;
    }
    
    /*
     * Pop Visitors
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Pop.TR p) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Pop.TM p) {
        return null;
    }

    /*
     * Push Visitors
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Push.TR p) {
        return null;
    }

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Push.TM p) {
        return null;
    }

    /*
     * Ret Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Ret.T r) {
        return null;
    }

    /*
     * Setcc Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Setcc.T s) {
        return null;
    }

    /*
     * Pair<List<Instr<Temp>>, List<Instr<Temp>>>ext Visitor
     */

    public Pair<List<Instr<Temp>>, List<Instr<Temp>>> visit(Text.T t) {
        return null;
    }

}
