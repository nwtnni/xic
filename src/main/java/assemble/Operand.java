package assemble;

/**
 * A wrapper class for all the possible operands for assembly
 * instructionsd.
 */
public class Operand {

    public static Operand register(Kind kind) {
        return new Operand(kind, null);
    }

    public static Operand memory(String name) {
        return new Operand(Kind.MEM, name);
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
        MEM ("MEM");

        public String name;

        private Kind(String name) {
            this.name = name;
        }
    }

    public Kind kind;
    private String name;

    private Operand(Kind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    @Override
    public String toString() {
        if (kind == Kind.MEM) {
            return name;
        }
        return kind.name;
    }
}