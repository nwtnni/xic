package assemble;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import assemble.instructions.*;
import assemble.instructions.BinOp.Kind;
import static assemble.instructions.InstrFactory.*;

import xic.XicInternalException;

public class TrivialAllocator extends Allocator {

    public static CompUnit<Reg> allocate(CompUnit<Temp> unit) {
        TrivialAllocator allocator = new TrivialAllocator(unit);
        return allocator.allocate();
    }

    /**
     * TrivialAllocator spills everything onto the stack, reserving
     * %r9, %r10 and %r11 as shuttle registers.
     * 
    * %r9 should only be used in BRSO memory addressing mode.
     */
    @Override
    protected Optional<Reg> allocate(Temp t, int index) {
        switch (t.kind) {

            // Allocate an ordinary temporary
            case TEMP:
                boolean existing = tempStack.containsKey(t.name);
                Mem<Reg> mem = loadTemp(t.name);
                Reg reg = null;
                switch (index) {
                    case 0:
                        reg = Reg.R10;
                        break;
                    case 1:
                        reg = Reg.R11;
                        break;
                    case 2:
                        reg = Reg.R9;
                        break;
                    default:
                        assert false;
                }
                if (existing) instrs.add(new Mov.RMR(mem, reg));
                return Optional.of(reg);

            // Get the fixed register
            case FIXED:
                return Optional.of(t.getRegister());
        }
        assert false;
        return null;
    }

    /**
     * Places a Temp result back onto its stack location.
     */
    private void store(Temp t) {
        instrs.add(new Mov.RRM(Reg.R11, loadTemp(t.name)));
    }

    private TrivialAllocator(CompUnit<Temp> unit) {
        super(unit);
    }

    private CompUnit<Reg> allocate() {
        for (FuncDecl<Temp> fn : unit.text) {
            allocate(fn);
        }
        return allocated;
    }

    private void allocate(FuncDecl<Temp> fn) {
        FuncDecl.R allocatedFn = new FuncDecl.R(fn);
        instrs = new ArrayList<>();
        tempStack = new HashMap<>();
        tempCounter = 0;
        maxArgs = 0;
        maxRets = 0;

        for (Instr<Temp> i : fn.stmts) {
            i.accept(this);
        }

        allocatedFn.stmts = instrs;
        allocated.text.add(allocatedFn);

        // Calculate number of words to shift %rsp
        int rsp = tempCounter + maxArgs + maxRets;
        // 16 byte alignment if needed
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        allocatedFn.setStackSize(rsp);
    }

    /*
     * BinOp Visitors
     */

    public Boolean visit(BinOp.TIR b) {
        super.visit(b);
        store(b.dest);
        return null;
    }

    public Boolean visit(BinOp.TMR b) {
        super.visit(b);
        store(b.dest);
        return null;
    }

    public Boolean visit(BinOp.TRR b) {
        super.visit(b);
        store(b.dest);
        return null;
    }

    /*
     * Lea Visitor
     */

    public Boolean visit(Lea.T l) {
        super.visit(l);
        store(l.dest);
        return null;
    }

    /*
     * Mov Visitors
     */

    public Boolean visit(Mov.TIR m) {
        super.visit(m);
        store(m.dest);
        return null;
    }

    public Boolean visit(Mov.TMR m) {
        super.visit(m);
        store(m.dest);
        return null;
    }

    public Boolean visit(Mov.TRR m) {
        super.visit(m);
        store(m.dest); 
        return null;
    }

    public Boolean visit(Mov.TLR m) {
        super.visit(m);
        store(m.dest);
        return null;
    }
}
