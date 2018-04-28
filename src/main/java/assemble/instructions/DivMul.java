package assemble.instructions;

import assemble.*;

public abstract class DivMul<S, A> extends Instr<A> {

    public enum Kind {
        MUL     ("imulq"),
        HMUL    ("imulq"),
        DIV     ("idivq"),
        MOD     ("idivq");
        String opcode;
        private Kind (String s) { opcode = s; }
    }

    public Kind kind;
    public S src;
    public A dest;

    private DivMul(Kind kind, S src, A dest) {
        this.kind = kind;
        this.src = src;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return kind.opcode + " " + src;
    }

    /*
     *
     * Abstract Assembly Instructions
     *
     */

    /**
     * Abstract register source addressing mode.
     */
    public static class TR extends DivMul<Temp, Temp> {
        public TR(Kind kind, Temp src) {
            super(kind, src, (kind == Kind.DIV || kind == Kind.MUL) ? Temp.RAX : Temp.RDX);
        }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract memory source addressing mode.
     */
    public static class TM extends DivMul<Mem<Temp>, Temp> {
        public TM(Kind kind, Mem<Temp> src) {
            super(kind, src, (kind == Kind.DIV || kind == Kind.MUL) ? Temp.RAX : Temp.RDX);
        }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /*
     *
     * Assembly Instructions
     *
     */

    /**
     * Register source addressing mode.
     */
    public static class RR extends DivMul<Reg, Reg> {
        public RR(Kind kind, Reg src) {
            super(kind, src, (kind == Kind.DIV || kind == Kind.MUL) ? Reg.RDX : Reg.RAX);
        }
    }

    /**
     * Memory source addressing mode.
     */
    public static class RM extends DivMul<Mem<Reg>, Reg> {
        public RM(Kind kind, Mem<Reg> src) {
            super(kind, src, (kind == Kind.DIV || kind == Kind.MUL) ? Reg.RDX : Reg.RAX);
        }
    }
}
