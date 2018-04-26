package assemble;

import java.util.HashSet;
import java.util.Set;

import static assemble.Temp.Kind.*;

/**
 * A wrapper class for all the possible operands for assembly instructions.
 */
public class Temp {

    // Special temp to be replaced with
    public final static Temp CALLEE_RET_ADDR = new Temp(MULT_RET, "CALLEE_RET_ADDR");

    /* Temp factory methods. ------------------------------------------ */

    /** 
     * Temp for callee args (reading args)
     * Fixed offset from %rbp:
     *      movq %rdi, a0
     *      movq %rsi, a1
     *      ...
     *      movq 8(%rbp), a6
     *      movq 16(%rbp), a7
     *      ...
     *      movq [8*(n-5)], an
     */
    protected static Temp calleeArg(int i) {
        if (i < 6) {
            return Temp.fixed(Config.getArg(i));
        }
        // Args 6+ read in reverse order from stack starting at 16(%rbp)
        // +1 for stored BP, +1 for stored PC
        return Temp.mem(Temp.fixed(Operand.RBP), Config.WORD_SIZE * (i - 6 + 2));
    }

    /** 
     * Temp for caller args (writing args)
     * Fixed offset from %rsp:
     *      movq a0, %rdi
     *      movq a1, %rsi
     *      ...
     *      movq a6, 0(%rsp)
     *      movq a7, 8(%rsp)
     *      ...
     *      movq an, [8*(n-6)](%rsp)
     */
    protected static Temp callerArg(int i) {
        if (i < 6) {
            return Temp.fixed(Config.getArg(i));
        }
        // Args 6+ pushed in reverse order to stack starting at (%rsp)
        return Temp.mem(Temp.fixed(Operand.RSP), Config.WORD_SIZE * (i - 6));
    }

    /** 
     * Temp for callee returns (writing returns)
     * Writing returns is something like
     *      movq r0, %rax
     *      movq r1, %rdx
     *      movq r2, 0(%RET_ADDR)
     *      movq r3, 8(%RET_ADDR)
     *      ...
     *      movq rn, [8*(n-2)](%RET_ADDR)
     * 
     * RET_ADDR is passed in as arg0 and will be decided at alloc
     */
    protected static Temp calleeRet(int i) {
        if (i < 2) {
            return Temp.fixed(Config.getRet(i));
        }
        // Rets 2+ written in reverse order to offset(ret_addr)
        int offset = Config.WORD_SIZE * (i - 2);
        return Temp.mem(CALLEE_RET_ADDR, offset);
    }

    /**
     * Temp for caller returns (read returns)
     * Fixed offset from %rsp based on number of args:
     * movq %rax, r0
     * movq %rdx, r1
     * movq off(%rsp), r2
     * move [off + 8](%rsp), r3
     * ...
     * mov [off + 8*(n-2)](%rsp), rn
     */
    protected static Temp callerRet(int i, int numArgs) {
        if (i < 2) {
            return Temp.fixed(Config.getRet(i));
        }
        // Rets 2+ read in reverse order from offset(%rsp)
        int offset = Config.WORD_SIZE * (i - 2 + Math.max(numArgs - 6, 0));
        return Temp.mem(Temp.fixed(Operand.RSP), offset);
    }

    /** Factory method for named temp. */
    protected static Temp temp(String name) {
        return new Temp(TEMP, name);
    }

    /** Factory method for a immediate temp. */
    public static Temp imm(long value) {
        return new Temp(value);
    }

    // Factory methods for 3 kinds of memory addressing modes
    
    /**
     * A memory access [b]
     * In the form: (base)
     */
    public static Temp mem(Temp b) {
        assert b != null && (b.isTemp() || b.isFixed());
        return new Temp(MEM, b, null, 0, 1);
    }

    /**
     * A memory access [b + off]
     * In the form: offset(base)
     */
    public static Temp mem(Temp b, int off) {
        assert b != null && (b.isTemp() || b.isFixed() || b.equals(CALLEE_RET_ADDR));
        assert off % Config.WORD_SIZE == 0;
        return new Temp(MEMBR, b, null, off, 1);
    }

    /**
     * A memory access [b + scale * r + off]
     * In the form offset(base,scale)
     * 
     * scale must be 1, 2, 4 or 8
     */
    public static Temp mem(Temp b, Temp r, int off, int scale) {
        assert b != null && (b.isTemp() || b.isFixed());
        assert r != null && (r.isTemp() || b.isFixed());
        assert off % Config.WORD_SIZE == 0;
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
        return new Temp(MEMSBR, b, r, off, scale);
    }

    // For temp of a fixed register
    public static Temp fixed(Operand reg) {
        assert reg.isReg();
        return new Temp(reg);
    }

    /* Temp implementation -------------------------------------------- */

    public enum Kind {
        TEMP, IMM, MEM, MEMBR, MEMSBR, MULT_RET, FIXED;
    }

    public Kind kind;

    /** The name of a TEMP */
    public String name;

    /** Value for IMM */
    public long value;

    /** Values for MEM, MEMBR and MEMSBR. */
    public Temp base;
    public Temp reg;
    public int scale;
    public int offset;

    /** Register for fixed temp. */
    Operand register;

    /** Constructor for a named temp. */
    private Temp(Kind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    /** Constructor for immediates. */
    private Temp(long value) {
        this.kind = IMM;
        this.value = value;
    }

    /** Constructor for a mem temp. */
    private Temp(Kind kind, Temp base, Temp reg, int offset, int scale) {
        this.kind = kind;
        this.base = base;
        this.reg = reg;
        this.offset = offset;
        this.scale = scale;
    }

    /** Constructor for a fixed temp. */
    private Temp(Operand reg) {
        this.kind = FIXED;
        this.register = reg;
    }

    public boolean isImm() {
        return kind == IMM;
    }

    public boolean isTemp() {
        return kind == TEMP;
    }

    public boolean isFixed() {
        return kind == FIXED;
    }

    public boolean isMultRet() {
        return kind == MULT_RET;
    }

    public boolean isMem() {
        return !(isImm() || isTemp() || isFixed());
    }

    // Adds constraint that all named temps are memory addresses for 
    // trivial allocation purposes
    public boolean trivialIsMem() {
        return kind == TEMP || 
            kind == MEM || 
            kind == MEMBR || 
            kind == MEMSBR || 
            kind == MULT_RET;
    }

    /**
     * Gets the set of temps and fixed temps associated with this temp.
     * Returns an empty set for IMM.
     */
    public Set<Temp> getTemps() {
        Set<Temp> temps = new HashSet<>();
        switch (kind) {
            case TEMP:
            case FIXED:
            case MULT_RET:
                temps.add(this);
                break;
            case MEMSBR:
                temps.add(reg);
            case MEM:
            case MEMBR:
                temps.add(base);
                break;
            case IMM:
        }
        return temps;
    }

    /**
     * Gets the register associated with this fixed temp.
     * Requires temp is fixed.
     */
    public Operand getRegister() {
        assert isFixed();
        return register;
    }

    @Override
    public int hashCode() {
        switch (kind) {
            case IMM:
                return Long.hashCode(value);
            case TEMP:
                return name.hashCode();
            case FIXED:
                return register.hashCode();
            case MULT_RET:
                return MULT_RET.hashCode();
            case MEM:
                return base.hashCode();
            case MEMBR:
                return 41 * base.hashCode() ^ 59 * Integer.hashCode(offset);
            case MEMSBR:
                return 43 * base.hashCode() ^ 61 * reg.hashCode() ^
                    71 * Integer.hashCode(offset) ^ 73 * Integer.hashCode(scale);
            default:
                assert false;
                return -1;
        }
    }

    /**
     * Temp equality:
     *      IMM by value
     *      TEMP by name
     *      MEM by offset, scale and recursive equals on base and reg
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Temp) {
            Temp t = (Temp) obj;
            if (isImm() && t.isImm()) {
                // IMM
                return value == t.value;
            } else if (isTemp() && t.isTemp()) {
                // TEMP
                return name.equals(t.name);
            } else if (isFixed() && t.isFixed()) {
                // FIXED
                return register.equals(t.register);
            } else if (isMem()) {
                if (isMultRet()) {
                    // MULT_RET
                    return t.isMultRet();
                } else if (kind == MEM) {
                    // MEM [base]
                    return base.equals(t.base);
                } else if (kind == MEMBR) {
                    // MEM off[base]
                    return base.equals(t.base) && offset == t.offset;
                } else if (kind == MEMSBR) {
                    // MEM off[base,reg,scale]
                    return base.equals(t.base) &&
                        reg.equals(t.reg) &&
                        offset == t.offset &&
                        scale == t.scale;
                }
            }
        }
        return false;
    }

    /**
     * toString() is used when outputing abstract assembly to dot files and to debug.
     */
    @Override
    public String toString() {
        switch(kind) {
            case TEMP:
                return name;
            case IMM:
                return "$" + Long.toString(value);
            case MEM:
                return "(" + base + ")";
            case MEMBR:
                return String.format("%d(%s)", offset, base);
            case MEMSBR:
                return String.format("%d(%s, %s, %d)", offset, base, reg, scale);
            case MULT_RET:
                return "(" + name + ")";
            case FIXED:
                return register.toString();
        }
        assert false;
        return null;
    }
}
