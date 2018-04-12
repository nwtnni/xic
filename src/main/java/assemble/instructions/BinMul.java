package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import assemble.*;

public class BinMul extends Instr {
    public enum Kind {
        MUL     ("imulq"),
        HMUL    ("imulq"),
        DIV     ("idivq"),
        MOD     ("idivq");
        String opcode;
        private Kind (String s) { opcode = s; }

    }

    public Kind kind;
    protected Temp leftTemp;
    protected Temp rightTemp;

    public Operand dest;
    public Operand left;
    public Operand right;

    public BinMul(Kind kind, Temp leftTemp, Temp rightTemp) {
        this.kind = kind;
        this.leftTemp = leftTemp;
        this.rightTemp = rightTemp;
        if (kind == Kind.MUL || kind == Kind.DIV) {
            this.dest = Operand.register(Operand.Kind.RAX);
        } else {
            this.dest = Operand.register(Operand.Kind.RDX);
        }
    }

    @Override
    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(String.format("movq %s, %%rax", left.toString()));
        if (kind == Kind.DIV || kind == Kind.MOD) {
                instrs.add("cqo");
        }
        instrs.add(String.format("%s %s", kind.opcode, right.toString()));
        return instrs;
    }
}