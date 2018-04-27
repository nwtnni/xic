package assemble;

import assemble.instructions.InstrFactory.*;

import util.Either;

/** 
 * Some special stack-related names that are used in the 
 * IR to assembly transation 
 */
public class Config {

    /** Word size; assumes a 64-bit architecture */
    public static final int WORD_SIZE = 8;

    /** _xi_alloc function name */
    public static final String XI_ALLOC = "_xi_alloc";
    
    /** _xi_out_of_bounds function name */
    public static final String XI_OUT_OF_BOUNDS = "_xi_out_of_bounds";

    /** Get the register for args 0-5 on System V. */
    public static Temp getArg(int i) {
        switch (i) {
            case 0:
                return Temp.RDI;
            case 1:
                return Temp.RSI;
            case 2:
                return Temp.RDX;
            case 3:
                return Temp.RCX;
            case 4:
                return Temp.R8;
            case 5:
                return Temp.R9;
        }
        assert false;
        return null;
    }

    /** Get the register for returns 0-1 on System V. */
    public static Temp getRet(int i) {
        if (i == 0) {
            return Temp.RAX;
        } else if (i == 1) {
            return Temp.RDX;
        }
        assert false;
        return null;
    }

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
    protected static Either<Temp, Mem<Temp>> calleeArg(int i) {

        if (i < 6) return Either.left(getArg(i));

        // Args 6+ read in reverse order from stack starting at 16(%rbp)
        // +1 for stored BP, +1 for stored PC
        Mem<Temp> mem = Mem.of(Temp.RBP, Config.WORD_SIZE * (i - 6 + 2));
        return Either.right(mem);
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
    protected static Either<Temp, Mem<Temp>> callerArg(int i) {
        if (i < 6) return Either.left(getArg(i));

        // Args 6+ pushed in reverse order to stack starting at (%rsp)
        Mem<Temp> mem = Mem.of(Temp.RSP, Config.WORD_SIZE * (i - 6));
        return Either.right(mem);
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
    protected static Either<Temp, Mem<Temp>> calleeRet(Temp addr, int i) {

        if (i < 2) return Either.left(getRet(i));

        // Rets 2+ written in reverse order to offset(ret_addr)
        Mem<Temp> mem = Mem.of(addr, WORD_SIZE * (i - 2));
        return Either.right(mem);
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
    protected static Either<Temp, Mem<Temp>> callerRet(int i, int numArgs) {

        if (i < 2) return Either.left(getRet(i));

        // Rets 2+ read in reverse order from offset(%rsp)
        int offset = Config.WORD_SIZE * (i - 2 + Math.max(numArgs - 6, 0));
        Mem<Temp> mem = Mem.of(Temp.RSP, offset);
        return Either.right(mem);
    }

    /**
     * Check if value is representable with n-bits in 2's complement notation
     * Loses MIN_INT for each n bits for simplicity
     */
    public static boolean within(int bits, long value) {
        return Math.abs(value) < Math.pow(2, bits - 1) - 1;
    }
}
