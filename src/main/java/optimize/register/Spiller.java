package optimize.register;

import java.util.*;

import assemble.*;
import assemble.instructions.*;
import util.*;

public class Spiller extends InstrVisitor<Pair<List<Instr<Temp>>, List<Instr<Temp>>>> {
    
    /*
     * BinOp Visitors
     */

    public T visit(BinOp.TIR b) {
        return null;
    }

    public T visit(BinOp.TIM b) {
        return null;
    }

    public T visit(BinOp.TRM b) {
        return null;
    }

    public T visit(BinOp.TMR b) {
        return null;
    }

    public T visit(BinOp.TRR b) {
        return null;
    }

    /*
     * Call Visitor
     */

    public T visit(Call.T c) {
        return null;
    }

    /*
     * Cmp Visitors
     */

    public T visit(Cmp.TIR c) {
        return null;
    }

    public T visit(Cmp.TRM c) {
        return null;
    }

    public T visit(Cmp.TMR c) {
        return null;
    }

    public T visit(Cmp.TRR c) {
        return null;
    }

    /*
     * Cqo Visitor
     */

    public T visit(Cqo.T c) {
        return null;
    }

    /*
     * DivMul Visitors
     */

    public T visit(DivMul.TR d) {
        return null;
    }

    public T visit(DivMul.TM d) {
        return null;
    }

    /*
     * Jcc Visitor
     */

    public T visit(Jcc.T j) {
        return null;
    }

    /*
     * Jmp Visitor
     */

    public T visit(Jmp.T j) {
        return null;
    }

    /*
     * Label Visitor
     */

    public T visit(Label.T l) {
        return null;
    }

    /*
     * Lea Visitor
     */

    public T visit(Lea.T l) {
        return null;
    }

    /*
     * Mov Visitors
     */

    public <L, R> T visit(Mov.TIR m) {
        return null;
    }

    public <L, R> T visit(Mov.TIM m) {
        return null;
    }

    public <L, R> T visit(Mov.TRM m) {
        return null;
    }

    public <L, R> T visit(Mov.TMR m) {
        return null;
    }

    public <L, R> T visit(Mov.TRR m) {
        return null;
    }
    
    /*
     * Pop Visitors
     */

    public T visit(Pop.TR p) {
        return null;
    }

    public T visit(Pop.TM p) {
        return null;
    }

    /*
     * Push Visitors
     */

    public T visit(Push.TR p) {
        return null;
    }

    public T visit(Push.TM p) {
        return null;
    }

    /*
     * Ret Visitor
     */

    public T visit(Ret.T r) {
        return null;
    }

    /*
     * Setcc Visitor
     */

    public T visit(Setcc.T s) {
        return null;
    }

    /*
     * Text Visitor
     */

    public T visit(Text.T t) {
        return null;
    }

}