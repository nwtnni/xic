package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import assemble.*;

public class BinOp extends Instr {
    public enum Kind {
        ADD     ("addq"),
        SUB     ("subq"),
        AND     ("andq"),
        OR      ("orq"),
        XOR     ("xorq"),
        LSHIFT  ("shlq"),
        RSHIFT  ("shrq"),
        ARSHIFT ("sarq");

        String opcode;
        private Kind (String s) { opcode = s; }
    }

    public Kind kind;
    public Temp destTemp;
    public Temp leftTemp;
    public Temp rightTemp;

    public Operand dest;
    public Operand src;

    public BinOp(Kind kind, Temp d, Temp l, Temp r) {
        this.kind = kind;
        this.destTemp = d;
        this.leftTemp = l;
        this.rightTemp = r;
    }

    public BinOp(Kind kind, Operand dest, Operand src) {
        this.kind = kind;
        this.dest = dest;
        this.src = src;
    }

    @Override
    public List<String> toAbstractAssembly() {
        if (this.leftTemp != null) {
            String instr = String.format("%s %s, %s", kind.opcode, leftTemp.toString(), rightTemp.toString());
            return Arrays.asList(instr);
        }
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        String instr = String.format("%s %s, %s", kind.opcode, src.toString(), dest.toString());
        return Arrays.asList(instr);
    }
}