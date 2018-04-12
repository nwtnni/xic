package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import ir.*;
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
    public Temp leftTemp;
    public Temp rightTemp;

    public Operand dest;
    public Operand src;

    public BinOp(Kind kind, Temp leftTemp, Temp rightTemp) {
        this.kind = kind;
        this.leftTemp = leftTemp;
        this.rightTemp = rightTemp;
    }

    @Override
    public List<String> toAssembly() {
        String instr = String.format("%s %s, %s", kind.opcode, src.toString(), dest.toString());
        return Arrays.asList(instr);
    }
}