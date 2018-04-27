package assemble.instructions;

import assemble.*;

public class Div<S, A> extends Instr<A> {

    public enum Kind {
        DIV     ("idivq"),
        MOD     ("idivq");
        String opcode;
        private Kind (String s) { opcode = s; }
    }

    public Kind kind;
    public S src;
    public A dest;

    private Div(Kind kind, S src, A dest) {
        this.kind = kind;
        this.src = src;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return kind.opcode + " " + src;
    }

    @Override
    public <T> T accept(InsVisitor<A, T> v) {
        return v.visit(this);
    }

    /*
     *
     * Abstract Assembly Instructions
     *
     */

    /**
     * Abstract register source addressing mode.
     */
    public static class TR extends Div<Temp, Temp> {
        public TR(Kind kind, Temp src) {
            super(kind, src, (kind == Kind.DIV) ? Temp.RDX : Temp.RAX);
        }
    }

    /**
     * Abstract memory source addressing mode.
     */
    public static class TM extends Div<Mem<Temp>, Temp> {
        public TM(Kind kind, Mem<Temp> src) {
            super(kind, src, (kind == Kind.DIV) ? Temp.RDX : Temp.RAX);
        }
    }

    /*
     *
     * Assembly Instructions
     *
     */

    /**
     * Register source addressing mode.
     */
    public static class RR extends Div<Temp, Reg> {
        public RR(Kind kind, Temp src) {
            super(kind, src, (kind == Kind.DIV) ? Reg.RDX : Reg.RAX);
        }
    }

    /**
     * Memory source addressing mode.
     */
    public static class RM extends Div<Mem<Temp>, Reg> {
        public RM(Kind kind, Mem<Temp> src) {
            super(kind, src, (kind == Kind.DIV) ? Reg.RDX : Reg.RAX);
        }
    }
}
