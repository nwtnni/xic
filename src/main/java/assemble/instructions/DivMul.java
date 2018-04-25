package assemble.instructions;

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
    public Temp destTemp;
    public Temp srcTemp;

    public Operand dest;
    public Operand src;

    public DivMul(Kind kind, Temp srcTemp) {
        this.kind = kind;
        this.srcTemp = srcTemp;

        // Intermediate register is fixed for these instructions
        if (kind == Kind.MUL || kind == Kind.DIV) {
            this.destTemp = Temp.fixed(Operand.RAX);
        } else {
            this.destTemp = Temp.fixed(Operand.RDX);
        }
    }

    @Override
    public String toAbstractAssembly() {
        return kind.opcode + " " + srcTemp;
    }

    @Override
    public String toAssembly() {
        return kind.opcode + " " + src;
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}