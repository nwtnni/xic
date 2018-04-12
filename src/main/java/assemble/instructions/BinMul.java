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
    public Temp destTemp;
    public Temp leftTemp;
    public Temp rightTemp;

    public Operand dest;
    private Operand reg;
    public Operand left;
    public Operand right;

    public BinMul(Kind kind, Temp d, Temp l, Temp r) {
        this.kind = kind;
        this.destTemp = d;
        this.leftTemp = l;
        this.rightTemp = r;

        // Destination is fixed for these instructions
        if (kind == Kind.MUL || kind == Kind.DIV) {
            this.reg = Operand.RAX;
        } else {
            this.reg = Operand.RDX;
        }
    }

    @Override
    public List<String> toAbstractAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(String.format("movq %s, %%rax", leftTemp));
        if (kind == Kind.DIV || kind == Kind.MOD) {
                instrs.add("cqo");
        }
        instrs.add(kind.opcode + " " + rightTemp);
        instrs.add(String.format("movq %s, %s", reg, destTemp));
        return instrs;
    }

    @Override
    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(String.format("movq %s, %%rax", left));
        if (kind == Kind.DIV || kind == Kind.MOD) {
                instrs.add("cqo");
        }
        instrs.add(kind.opcode + " " + right);
        instrs.add(String.format("movq %s, %s", reg, dest));
        return instrs;
    }
}