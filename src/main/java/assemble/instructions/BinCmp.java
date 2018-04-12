package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import assemble.*;

public class BinCmp extends Instr {
    public enum Kind {
        EQ  ("e"),
        NEQ ("ne"),
        LT  ("l"),
        GT  ("g"),
        LEQ ("le"),
        GEQ ("ge");

        String flag;
        private Kind (String s) { flag = s; }
    }

    public Kind kind;
    public Temp destTemp;
    protected Temp leftTemp;
    protected Temp rightTemp;

    public Operand destination;
    public Operand left;
    public Operand right;

    public BinCmp(Kind kind, Temp d, Temp l, Temp r) {
        this.kind = kind;
        this.destTemp = d;
        this.leftTemp = l;
        this.rightTemp = r;

        // Destination is fixed for these instructions
        this.destination = Operand.reg(Operand.Kind.RAX);
    }

    @Override
    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(String.format("cmpq %s, %s", left.toString(), right.toString()));
        instrs.add("movq $0, %rax");
        instrs.add(String.format("set%s %%al", kind.flag));
        return instrs;
    }
}