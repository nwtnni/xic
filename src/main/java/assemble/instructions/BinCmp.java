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
    public Temp leftTemp;
    public Temp rightTemp;

    public Operand dest;
    private Operand reg;
    public Operand left;
    public Operand right;

    public BinCmp(Kind kind, Temp d, Temp l, Temp r) {
        this.kind = kind;
        this.destTemp = d;
        this.leftTemp = l;
        this.rightTemp = r;

        // Destination is fixed for these instructions
        this.dest = Operand.RAX;
    }

    @Override
    public List<String> toAbstractAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(String.format("cmpq %s, %s", leftTemp, rightTemp));
        instrs.add("movq $0, %rax");
        instrs.add(String.format("set%s %%al", kind.flag));
        instrs.add(String.format("movq %s, %s", reg, destTemp));
        return instrs;
    }

    @Override
    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(String.format("cmpq %s, %s", left, right));
        instrs.add("movq $0, %rax");
        instrs.add(String.format("set%s %%al", kind.flag));
        instrs.add(String.format("movq %s, %s", reg, dest));
        return instrs;
    }
}