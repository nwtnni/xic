package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import assemble.*;

public class DivMul extends Instr {
    public enum Kind {
        MUL     ("imulq"),
        HMUL    ("imulq"),
        DIV     ("idivq"),
        MOD     ("idivq");
        String opcode;
        private Kind (String s) { opcode = s; }

    }

    public Kind kind;
    public Temp srcTemp;

    public Operand dest;
    public Operand src;

    public DivMul(Kind kind, Temp srcTemp) {
        this.kind = kind;
        this.srcTemp = srcTemp;

        // Intermediate register is fixed for these instructions
        if (kind == Kind.MUL || kind == Kind.DIV) {
            this.dest = Operand.RAX;
        } else {
            this.dest = Operand.RDX;
        }
    }

    @Override
    public List<String> toAbstractAssembly() {
        return Arrays.asList(kind.opcode + " " + srcTemp);
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(kind.opcode + " " + src);
    }

}