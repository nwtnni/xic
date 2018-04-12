package assemble;

/**
 * A wrapper class for all the possible operands for assembly
 * instructionsd.
 */
public class Operand {

    public static Operand register(Kind kind) {
        return new Operand(kind, null, 0);
    }

    public static Operand memory(String mem) {
        return new Operand(Kind.MEM, mem, 0);
    }

    public static Operand immediate(long value) {
        return new Operand(Kind.IMM, null, value);
    }

    public enum Kind {
        RAX ("%rax"),
        RBX ("%rbx"),
        RCX ("%rcx"),
        RDX ("%rdx"),
        RSI ("%rsi"),
        RDI ("%rdi"),
        RBP ("%rbp"),
        RSP ("%rsp"),
        R8 ("%r8"),
        R9 ("%r9"),
        R10 ("%r10"),
        R11 ("%r11"),
        R12 ("%r12"),
        R13 ("%r13"),
        R14 ("%r14"),
        R15 ("%r15"),
        MEM ("MEM"),
        IMM ("IMM");

        public String name;

        private Kind(String name) {
            this.name = name;
        }
    }

    public Kind kind;
    private String mem;
    private long value;

    private Operand(Kind kind, String mem, long value) {
        this.kind = kind;
        this.mem = mem;
        this.value = value;
    }

    @Override
    public String toString() {
        if (kind == Kind.MEM) {
            return mem;
        } else if (kind == Kind.IMM) {
            return "$" + Long.toString(value);
        }
        return kind.name;
    }
}