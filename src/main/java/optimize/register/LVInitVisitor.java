package optimize.register;

import java.util.Set;

import assemble.*;
import assemble.instructions.*;

/**
 * Annotates instructions with use and def sets for liveness analysis.
 */
public class LVInitVisitor extends InsVisitor<Void> {

    public static void init(FuncDecl fn) {
        LVInitVisitor visitor = new LVInitVisitor();
        for (Instr in : fn.stmts) {
            in.accept(visitor);
        }
    }

    public Void visit(BinOp i) {
        i.use = i.srcTemp.getTemps();
        i.use.addAll(i.destTemp.getTemps());

        if (i.destTemp.isFixed() || i.destTemp.isTemp()) {
            i.def = i.destTemp.getTemps();
        }
        return null;
    }

    public Void visit(Call i) {
        i.def = Set.of(Temp.RAX, Temp.RCX, Temp.RDX, Temp.RSI, Temp.RDI, Temp.R8, Temp.R9, Temp.R10, Temp.R11);
        return null;
    }

    public Void visit(Cmp i) {
        i.use = i.leftTemp.getTemps();
        i.use.addAll(i.rightTemp.getTemps());
        return null;
    }

    public Void visit(Cqo i) {
        i.use = Set.of(Temp.RAX);
        i.def = Set.of(Temp.RDX);
        return null;
    }

    public Void visit(DivMul i) {
        i.use = i.srcTemp.getTemps();
        i.use.add(Temp.RAX);
        if (i.isDivOrMod()) {
            i.use.add(Temp.RDX);
        }
        i.def = Set.of(Temp.RAX, Temp.RDX);
        return null;
    }

    public Void visit(Jcc i) {
        return null;
    }

    public Void visit(Jmp i) {
        return null;
    }

    public Void visit(Label i) {
        return null;
    }

    public Void visit(Lea i) {
        i.use = i.srcTemp.getTemps();
        i.def = i.destTemp.getTemps();
        return null;
    }

    public Void visit(Mov i) {
        i.use = i.srcTemp.getTemps();

        if (i.destTemp.isFixed() || i.destTemp.isTemp()) {
            i.def = i.destTemp.getTemps();
        } else {
            i.use.addAll(i.destTemp.getTemps());
        }
        return null;
    }

    public Void visit(Pop i) {
        i.def = Set.of(Temp.fixed(i.operand));
        return null;
    }

    public Void visit(Push i) {
        i.use = Set.of(Temp.fixed(i.operand));
        return null;
    }


    public Void visit(Ret i) {
        return null;
    }

    public Void visit(Setcc i) {
        i.def = Set.of(Temp.RAX);
        return null;
    }

    public Void visit(Text i) {
        return null;
    }
}
