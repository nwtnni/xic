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
    public Temp srcTemp;

    public Operand dest;
    public Operand src;

    /**
     * Creates a BinOp with abstract operands.
     */
    public BinOp(Kind kind, Temp d, Temp s) {
        this.kind = kind;
        this.destTemp = d;
        this.srcTemp = s;
    }

    /** 
     * Create a BinOp with real operands. Should not be used until allocation.
     */
    public BinOp(Kind kind, Operand dest, Operand src) {
        this.kind = kind;
        this.dest = dest;
        this.src = src;
    }

    @Override
    public List<String> toAbstractAssembly() {
        return Arrays.asList(String.format("%s %s, %s", kind.opcode, srcTemp, destTemp));
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(String.format("%s %s, %s", kind.opcode, src, dest));
    }

}