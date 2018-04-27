package assemble.instructions;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import assemble.*;
import ir.*;
import util.Pair;

public abstract class InstrFactory {

    /*
     * BinOp Factory Methods
     */

    public static BinOp<Imm, Temp, Temp> binOpIR(BinOp.Kind kind, Imm src, Temp dest) {
        return new BinOp.TIR(kind, src, dest);
    }

    public static BinOp<Imm, Mem<Temp>, Temp> binOpIM(BinOp.Kind kind, Imm src, Mem<Temp> dest) {
        return new BinOp.TIM(kind, src, dest);
    }

    public static BinOp<Temp, Mem<Temp>, Temp> binOpRM(BinOp.Kind kind, Temp src, Mem<Temp> dest) {
        return new BinOp.TRM(kind, src, dest);
    }

    public static BinOp<Mem<Temp>, Temp, Temp> binOpMR(BinOp.Kind kind, Mem<Temp> src, Temp dest) {
        return new BinOp.TMR(kind, src, dest);
    }

    public static BinOp<Temp, Temp, Temp> binOpRR(BinOp.Kind kind, Temp src, Temp dest) {
        return new BinOp.TRR(kind, src, dest);
    }

    /**
     * Assign a proper BinOp tiling for an unknown combination of operands.
     */
    public static List<Instr<Temp>> binOp(
        BinOp.Kind kind,
        Operand src,
        Operand dest
    ) {

        // src is register, dest is register
        if (src.isTemp() && dest.isTemp()) return List.of(binOpRR(kind, src.getTemp(), dest.getTemp()));

        // src is register, dest is memory
        else if (src.isTemp() && dest.isMem()) return List.of(binOpRM(kind, src.getTemp(), dest.getMem()));

        // src is memory, dest is register
        else if (src.isMem() && dest.isTemp()) return List.of(binOpMR(kind, src.getMem(), dest.getTemp()));

        // Both src and dest are in memory; must shuttle one
        Temp shuttle = TempFactory.generate("bin_op_shuttle");
        return List.of(
            movMR(src.getMem(), shuttle),
            binOpRM(kind, shuttle, dest.getMem())
        );
    }


    /**
     * Generate a proper BinOp tiling of left and right operands.
     */
    public static Pair<Operand, List<Instr<Temp>>> binOp(
        BinOp.Kind kind,
        Operand left,
        Operand right,
        Optional<Imm> immL,
        Optional<Imm> immR
    ) {
        List<Instr<Temp>> instrs = new ArrayList<>();

        Temp destT = TempFactory.generate("bin_op_result");
        Operand dest = Operand.temp(destT);

        if (immL.isPresent() && immR.isPresent()) {
            // imm op imm

            // Shuttle left imm into register
            instrs.add(movIR(immL.get(), destT));

            // Also shuttle right imm64 into register
            if (!Config.within(32, immR.get().getValue())) {
                Temp shuttle = TempFactory.generate("bin_op_shuttle");
                instrs.add(movIR(immR.get(), shuttle));
                instrs.add(binOpRR(kind, shuttle, destT));

            // Otherwise don't need to shuttle right
            } else {
                instrs.add(binOpIR(kind, immR.get(), destT));
            }
        } else if (immL.isPresent()) {
            // imm op r/m64

            // Can't commute subtraction
            if (kind == BinOp.Kind.SUB) {
                instrs.add(movIR(immL.get(), destT));
                instrs.addAll(binOp(kind, right, dest));

            // Otherwise commute as immR case
            } else {
                instrs.addAll(mov(right, dest));

                // Shuttle left imm64
                if (!Config.within(32, immL.get().getValue())) {
                    Temp shuttle = TempFactory.generate("bin_op_shuttle");
                    instrs.add(movIR(immL.get(), shuttle));
                    instrs.add(binOpRR(kind, shuttle, destT));

                // Otherwise add imm directly
                } else {
                    instrs.add(binOpIR(kind, immL.get(), destT));
                }
            }
        } else if (immR.isPresent()) {
            // r/m64 op imm

            instrs.addAll(mov(left, dest));

            // Shuttle right imm64
            if (!Config.within(32, immR.get().getValue())) {
                Temp shuttle = TempFactory.generate("bin_op_shuttle");
                instrs.add(movIR(immR.get(), shuttle));
                instrs.add(binOpRR(kind, shuttle, destT));

            // Otherwise add imm directly
            } else {
                instrs.add(binOpIR(kind, immR.get(), destT));
            }
        } else {
            // General case of r/m64 op r/m64
            instrs.addAll(mov(left, dest));
            instrs.addAll(binOp(kind, right, dest));
        }

        return new Pair<>(dest, instrs);
    }

    /**
     * Generate tiling for DivMul instructions with left and right
     */
    public static Pair<Operand, List<Instr<Temp>>> divMul(
        DivMul.Kind kind,
        Operand left,
        Operand right,
        Optional<Imm> immL,
        Optional<Imm> immR
    ) {
        List<Instr<Temp>> instrs = new ArrayList<>();
        
        // Always shuttle left into RAX
        if (immL.isPresent()) {
            instrs.add(movIR(immL.get(), Temp.RAX));
        } else {
            instrs.addAll(mov(left, Operand.temp(Temp.RAX)));
        }

        // Shuttle right if imm
        if (immR.isPresent()) {
            Temp shuttle = TempFactory.generate("div_mul_shuttle");
            instrs.add(movIR(immR.get(), shuttle));

            DivMul<Temp, Temp> op = divMulR(kind, shuttle);
            instrs.add(op);

            return new Pair<>(Operand.temp(op.dest), instrs);

        // Can use r/m64 directly
        } else {
            // General case of r/m64 op r/m64

            if (right.isMem()) {
                DivMul<Mem<Temp>, Temp> op = divMulM(kind, right.getMem());
                instrs.add(op);
                return new Pair<>(Operand.mem(op.src), instrs);
            } else {
                DivMul<Temp, Temp> op = divMulR(kind, right.getTemp());
                instrs.add(op);
                return new Pair<>(Operand.temp(op.src), instrs);
            }
        }
    }

    /*
     * Call Factory Method
     */

    public static Call<Temp> call(String name, int numArgs, int numRets) {
        return new Call.T(name, numArgs, numRets);
    }

    /*
     * Cmp Factory Methods
     */

    // TODO: check that [left] is 32 bits or less, otherwise spill into temp
    public static Cmp<Imm, Temp, Temp> cmpIR(Imm left, Temp right) {
        return new Cmp.TIR(left, right);
    }

    public static Cmp<Temp, Mem<Temp>, Temp> cmpRM(Temp left, Mem<Temp> right) {
        return new Cmp.TRM(left, right);
    }

    public static Cmp<Mem<Temp>, Temp, Temp> cmpMR(Mem<Temp> left, Temp right) {
        return new Cmp.TMR(left, right);
    }

    public static Cmp<Temp, Temp, Temp> cmpRR(Temp left, Temp right) {
        return new Cmp.TRR(right, left);
    }

    // TODO: Currently this is the only Cmp factory that flips its arguments for the
    // user; should be standardized
    public static List<Instr<Temp>> cmpII(Imm left, Imm right) {

        boolean lfit = Config.within(32, left.getValue());
        boolean rfit = Config.within(32, right.getValue());

        // Shuttle both into registers if too large
        if (!rfit) {
            Temp shuttleA = TempFactory.generate("cmpII_shuttle_A");
            Temp shuttleB = TempFactory.generate("cmpII_shuttle_B");

            return List.of(
                movIR(left, shuttleA), 
                movIR(right, shuttleB),
                cmpRR(shuttleB, shuttleA)
            );
        }

        // Otherwise shuttle smaller one into register
        Temp shuttle = TempFactory.generate("cmpII_shuttle");
        return List.of(
            movIR(left, shuttle),
            cmpIR(right, shuttle)
        );
    }

    /**
     * Assign a proper Cmp tiling for an unknown combination of operands.
     */
    public static List<Instr<Temp>> cmp(
        Operand l,
        Operand r
    ) {

        // Semantics of L vs. R reversed here due to AT&T syntax

        // left is register, right is register
        if (l.isTemp() && r.isTemp()) return List.of(cmpRR(r.getTemp(), l.getTemp()));

        // left is register, right is memory
        else if (l.isTemp() && r.isMem()) return List.of(cmpMR(r.getMem(), l.getTemp()));

        // left is memory, right is register
        else if (l.isMem() && r.isTemp()) return List.of(cmpRM(r.getTemp(), l.getMem()));

        // Both src and dest are in memory; must shuttle one
        Temp shuttle = TempFactory.generate("cmp_shuttle");
        return List.of(
            movMR(r.getMem(), shuttle),
            cmpRM(shuttle, l.getMem())
        );
    }

    /*
     * Cqo Factory Method
     */

    public static Cqo<Temp> cqo() {
        return new Cqo.T();
    }

    /*
     * DivMul Factory Methods
     */

    public static DivMul<Temp, Temp> divMulR(DivMul.Kind kind, Temp src) {
        return new DivMul.TR(kind, src);
    }

    public static DivMul<Mem<Temp>, Temp> divMulM(DivMul.Kind kind, Mem<Temp> src) {
        return new DivMul.TM(kind, src);
    }

    /*
     * Jcc Factory Method
     */

    public static Jcc<Temp> jcc(Jcc.Kind kind, IRLabel target) {
        return new Jcc.T(kind, target);
    }

    /*
     * Jmp Factory Methods
     */

    public static Jmp<Temp> jmpFromLabel(Label<Temp> label) {
        return new Jmp.T(label);
    }

    public static Jmp<Temp> jmpFromIRJump(IRJump jump) {
        return new Jmp.T(jump);
    }

    /*
     * Label Factory Methods
     */

    public static Label<Temp> labelFromIRLabel(IRLabel l) {
        return new Label.T(l);
    }

    public static Label<Temp> labelFromFn(IRFuncDecl fn) {
        return new Label.T(fn.name() + ":");
    }

    public static Label<Temp> labelFromRet(IRFuncDecl fn) {
        return new Label.T("_RET_" + fn.name() + ":");
    }

    /*
     * Lea Factory Method
     */

    public static Lea<Temp> lea(Mem<Temp> src, Temp dest) {
        return new Lea.T(dest, src);    
    }

    /*
     * Mov Factory Methods
     */

    public static Mov<Imm, Temp, Temp> movIR(Imm src, Temp dest) {
        return new Mov.TIR(src, dest);
    }

    public static Mov<Imm, Mem<Temp>, Temp> movIM(Imm src, Mem<Temp> dest) {
        return new Mov.TIM(src, dest);
    }

    public static Mov<Temp, Mem<Temp>, Temp> movRM(Temp src, Mem<Temp> dest) {
        return new Mov.TRM(src, dest);
    }

    public static Mov<Mem<Temp>, Temp, Temp> movMR(Mem<Temp> src, Temp dest) {
        return new Mov.TMR(src, dest);
    }

    public static Mov<Temp, Temp, Temp> movRR(Temp src, Temp dest) {
        return new Mov.TRR(src, dest);
    }

    /**
     * Assign a proper Mov tiling for an unknown combination of operands.
     */
    public static List<Instr<Temp>> mov(
        Operand src,
        Operand dest
    ) {

        // src is register, dest is register
        if (src.isTemp() && dest.isTemp()) return List.of(movRR(src.getTemp(), dest.getTemp()));

        // src is register, dest is memory
        else if (src.isTemp() && dest.isMem()) return List.of(movRM(src.getTemp(), dest.getMem()));

        // src is memory, dest is register
        else if (src.isMem() && dest.isTemp()) return List.of(movMR(src.getMem(), dest.getTemp()));

        // Both src and dest are in memory; must shuttle one
        Temp shuttle = TempFactory.generate("mov_shuttle");
        return List.of(
            movMR(src.getMem(), shuttle),
            movRM(shuttle, dest.getMem())
        );
    }

    /*
     * Pop Factory Methods
     */

    public static Pop<Temp, Temp> popR(Temp dest) {
        return new Pop.TR(dest);
    }

    public static Pop<Mem<Temp>, Temp> popM(Mem<Temp> dest) {
        return new Pop.TM(dest);
    }
    
    /*
     * Push Factory Methods
     */

    public static Push<Temp, Temp> pushR(Temp src) {
        return new Push.TR(src);
    }

    public static Push<Mem<Temp>, Temp> pushM(Mem<Temp> src) {
        return new Push.TM(src);
    }

    /*
     * Ret Factory Method
     */

    public static Ret<Temp> ret() {
        return new Ret.T();
    }

    /*
     * Setcc Factory Method
     */

    public static Setcc<Temp> setcc(Setcc.Kind kind) {
        return new Setcc.T(kind); 
    }

    /*
     * Text Factory Method
     */

    public static Text<Temp> comment(String c) {
        return new Text.T("# " + c);
    }

    public static Text<Temp> text(String t) {
        return new Text.T(t);
    }

    public static Text<Temp> label(String l) {
        return new Text.T(l + ":");
    }
}
