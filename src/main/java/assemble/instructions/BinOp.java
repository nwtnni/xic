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
    public Operand left;
    public Operand right;

    public BinOp(Kind kind, Temp d, Temp l, Temp r) {
        this.kind = kind;
        this.destTemp = d;
        this.leftTemp = l;
        this.rightTemp = r;
    }

    public BinOp(Kind kind, Operand dest, Operand left, Operand right) {
        this.kind = kind;
        this.dest = dest;
        this.left = left;
        this.right = right;
    }

    @Override
    public List<String> toAbstractAssembly() {
        if (this.leftTemp != null) {
            String mov = String.format("movq %s, %s", leftTemp.toString(), destTemp.toString());
            String op = String.format("%s %s, %s", kind.opcode, rightTemp.toString(), destTemp.toString());
            return Arrays.asList(mov, op);
        }
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        String mov = String.format("movq %s, %s", left.toString(), dest.toString());
        String op = String.format("%s %s, %s", kind.opcode, right.toString(), dest.toString());
        return Arrays.asList(mov, op);
    }
}