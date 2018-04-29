package assemble;

/**
 * Represents a register operand for assembly.
 */
public enum Reg {

    RAX ("%rax"),
    RBX ("%rbx"),
    RCX ("%rcx"),
    RDX ("%rdx"),
    RSI ("%rsi"),
    RDI ("%rdi"),
    RBP ("%rbp"),
    RSP ("%rsp"),
    R8  ("%r8"),
    R9  ("%r9"),
    R10 ("%r10"),
    R11 ("%r11"),
    R12 ("%r12"),
    R13 ("%r13"),
    R14 ("%r14"),
    R15 ("%r15");

    public String name;

    private Reg(String name) {
        this.name = name;
    }

    /**
     * True if this register is caller-saved according to x86_64 conventions.
     */
    public boolean isCallerSaved() {
        switch (this) {
        case RAX:
        case RCX:
        case RDX:
        case RSI:
        case RDI:
        case R8:
        case R9:
        case R10:
        case R11:
            return true;
        default:
            return false;
        }
    }

    /**
     * True if this register is callee-saved according to x86_64 conventions.
     */
    public boolean isCalleeSaved() {
        switch (this) {
        case RSP:
        case RBP:
        case RBX:
        case R12:
        case R13:
        case R14:
        case R15:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns the AT&T String representation of this register.
     */
    @Override
    public String toString() {
        return name;
    }
}
