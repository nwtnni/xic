package assemble;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static assemble.instructions.BinOp.Kind.*;
import static assemble.instructions.DivMul.Kind.*;
import static assemble.instructions.Setcc.Kind.*;
import static assemble.instructions.InstrFactory.*;

import assemble.instructions.*;
import assemble.Config;
import emit.ABIContext;
import emit.Library;
import interpret.Configuration;
import ir.*;
import ir.IRBinOp.OpType;
import ir.IRMem.MemType;
import xic.XicInternalException;

import util.Pair;

/**
 * Converts IR into abstract assembly.
 */
public class Tiler extends IRVisitor<Operand> {

    /**
     * Returns the list of abstract assembly code given an canonical IR tree
     */
    public static CompUnit<Temp> tile(IRNode t, ABIContext c) {
        TempFactory.reset();
        Tiler tiler = new Tiler(c);
        t.accept(tiler);
        return tiler.unit;
    }

    public static final boolean INCLUDE_COMMENTS = false;

    // Mangled names context
    private ABIContext context;

    // Running list of assembly instructions
    private CompUnit<Temp> unit;

    // Current list of instructions
    List<Instr<Temp>> instrs;

    // Number of args passed to a called function
    private int callerNumArgs;

    // Callee multiple return address stored at this temp
    private Temp calleeReturnAddress;

    // Return label of current function visited
    private Label<Temp> returnLabel;

    private Tiler(ABIContext c) {
        this.context = c;
        this.unit = new CompUnit<>();
        this.instrs = new ArrayList<>();
        this.callerNumArgs = 0;
        this.returnLabel = null;
}

    /**
     * Checks if this node is an IRConst, and returns the corresponding
     * Imm if it is.
     */
    private Optional<Imm> checkImm(IRNode node) {
        if (!(node instanceof IRConst)) return Optional.empty();

        IRConst c = (IRConst) node;
        return Optional.of(new Imm(c.value()));
    }

    /*
     * Visitor methods ---------------------------------------------------------------------
     */
    
    public Operand visit(IRCompUnit c) {
        unit.name = c.name();
        for (IRFuncDecl fn : c.functions().values()) {
            fn.accept(this);
        }
        return null;
    }

    public Operand visit(IRFuncDecl f) {

        // Reset instance variables for each function
        instrs = new ArrayList<>();

        // Set return label
        returnLabel = labelFromRet(f);

        // Tile the function body
        f.body().accept(this);

        // Spill callee saved registers
        instrs.add(0, movRR(Temp.RBX, new Temp("_STORE_RBX")));
        instrs.add(0, movRR(Temp.R12, new Temp("_STORE_R12")));
        instrs.add(0, movRR(Temp.R13, new Temp("_STORE_R13")));
        instrs.add(0, movRR(Temp.R14, new Temp("_STORE_R14")));
        instrs.add(0, movRR(Temp.R15, new Temp("_STORE_R15")));

        // Set up prologue and epilogue
        FuncDecl.T fn = new FuncDecl.T(f, f.args(), f.rets(), instrs);
        unit.text.add(fn);
        
        return null;
    }

    public Operand visit(IRBinOp b) {
        Optional<Imm> immL = checkImm(b.left());
        Optional<Imm> immR = checkImm(b.right());

        Operand left = b.left().accept(this);
        Operand right = b.right().accept(this);
        
        BinOp.Kind bop = null;
        switch (b.type()) {
            case ADD:
                bop = ADD;
                break;
            case SUB:
                bop = SUB;
                break;
            case AND:
                bop = AND;
                break;
            case OR:
                bop = OR;
                break;
            case XOR:
                bop = XOR;
                break;
            default:
        }

        if (bop != null) {
            Pair<Operand, List<Instr<Temp>>> tiling = binOp(bop, left, right, immL, immR);
            instrs.addAll(tiling.second);
            return tiling.first;
        }

        DivMul.Kind uop = null;
        switch (b.type()) {
            case MUL:
                uop = MUL;
                break;
            case HMUL:
                uop = HMUL;
                break;
            case DIV:
                uop = DIV;
                break;
            case MOD:
                uop = MOD;
                break;
            default:
        }

        if (uop != null) {
            Pair<Operand, List<Instr<Temp>>> tiling = divMul(uop, left, right, immL, immR);
            instrs.addAll(tiling.second);
            return tiling.first;
        }
            
        Setcc.Kind flag = null;
        switch (b.type()) {
            case EQ:
                flag = EQ;
                break;
            case NEQ:
                flag = NEQ;
                break;
            case LT:
                flag = LT;
                break;
            case GT:
                flag = GT;
                break;
            case LEQ:
                flag = LEQ;
                break;
            case GEQ:
                flag = GEQ;
                break;
            default:
        }
        instrs.addAll(cmp(right, left, immR, immL));
        // TODO: this is sub-optimal use of setcc which can use other registers
        instrs.add(movIR(new Imm(0), Temp.RAX));
        instrs.add(setcc(flag));
        Temp t = TempFactory.generate("cmp");
        instrs.add(movRR(Temp.RAX, t));
        return Operand.temp(t);
    }
    
    public Operand visit(IRCall c) {

        callerNumArgs = c.numArgs();
        int numRets = c.numRets();

        // Arguments
        for (int i = 0; i < c.numArgs(); i++) {

            // Multiple return address (injected by Emitter)
            if (c.get(i).equals(Library.CALLER_MULT_RET)) {
                Mem<Temp> callerRet = Config.callerRet(2, callerNumArgs).getMem();
                instrs.add(lea(callerRet, Config.callerArg(i).getTemp()));
            
            // Ordinary argument (including object reference injected by Emitter)
            } else {
                Optional<Imm> imm = checkImm(c.get(i));
                Operand val = c.get(i).accept(this);
                instrs.addAll(mov(val, Config.callerArg(i), imm));
            }
        }

        // Function calls
        if (c.target() instanceof IRName) {
            String target = ((IRName) c.target()).name();
            instrs.add(callS(target, callerNumArgs, numRets));

        // Method calls
        } else if (c.target() instanceof IRTemp) {
            Operand target = c.target().accept(this);
            if (target.isTemp()) {
                instrs.add(callT(target.getTemp(), callerNumArgs, numRets));
            } else {
                instrs.add(callM(target.getMem(), callerNumArgs, numRets));
            }
        }

        return Config.callerRet(0, callerNumArgs);
    }

    public Operand visit(IRCJump c) {
        if (c.cond instanceof IRBinOp) {
            IRBinOp bop = (IRBinOp) c.cond;

            Operand left = bop.left.accept(this);
            Operand right = bop.right.accept(this);

            Optional<Imm> immL = checkImm(bop.left());
            Optional<Imm> immR = checkImm(bop.right());

            instrs.addAll(cmp(right, left, immR, immL));

            Jcc.Kind flag = null;
            switch (bop.type()) {
                case EQ:
                    flag = Jcc.Kind.E;
                    break;
                case NEQ:
                    flag = Jcc.Kind.NE;
                    break;
                case LT:
                    flag = Jcc.Kind.L;
                    break;
                case GT:
                    flag = Jcc.Kind.G;
                    break;
                case LEQ:
                    flag = Jcc.Kind.LE;
                    break;
                case GEQ:
                    flag = Jcc.Kind.GE;
                    break;
                case XOR:
                    flag = Jcc.Kind.NE;
                    break;
                default:
                    throw XicInternalException.runtime("Invalid binop for CJUMP.");
            }
            instrs.add(jcc(flag, c.trueLabel()));
            return null;
        }

        Optional<Imm> imm = checkImm(c.cond);

        // Immediate condition gets converted to jump or fall-through
        if (imm.isPresent()) {
            // Fall-through
            if (imm.get().getValue() == 0) {

            // Jmp to label
            } else if (imm.get().getValue() == 1) {
                Label<Temp> target = labelFromIRLabel(c.trueLabel());
                instrs.add(jmpFromLabel(target));

            // Otherwise something is broken
            } else {
                assert false;
            }
            return null;
        }

        // Otherwise generic cjump expression
        Operand cond = c.cond.accept(this);
        if (cond.isTemp()) {
            instrs.add(cmpIR(new Imm(1), cond.getTemp()));
        } else {
            // Must shuttle due to addressing modes for cmp
            Temp shuttle = TempFactory.generate("cjump");
            instrs.add(movIR(new Imm(1), shuttle));
            instrs.add(cmpRM(shuttle, cond.getMem()));
        }
        instrs.add(jcc(Jcc.Kind.Z, c.trueLabel()));
        return null;
    }

    public Operand visit(IRConst c) {
        // TODO: make sure we catch all constants
        return null;
    }

    public Operand visit(IRJump j) {
        instrs.add(jmpFromIRJump(j));
        return null;
    }

    public Operand visit(IRESeq e) {
        throw XicInternalException.runtime("IRESeq is not canonical.");
    }

    public Operand visit(IRExp e) {
        e.expr().accept(this);
        return null;   
    }

    public Operand visit(IRLabel l) {
        instrs.add(labelFromIRLabel(l));
        return null;
    }

    public Operand visit(IRMem m) {
        if (m.isGlobal()) {
            throw XicInternalException.runtime("found global in invalid instruction");
        }

        // Make allocator use addressing modes for immutable memory accesses
        if (m.isImmutable() && m.expr() instanceof IRBinOp) {

            IRBinOp bop = (IRBinOp) m.expr();
            assert bop.type() == OpType.ADD;

            if (bop.left() instanceof IRTemp) { 

                // B + off
                if (bop.right() instanceof IRConst) {

                    Operand base = bop.left().accept(this);
                    Imm offset = checkImm(bop.right()).get();

                    // Off must be within 32 bits
                    assert Config.within(32, offset.getValue());

                    // B must be a temp, not nested memory access
                    if (base.isTemp()) {
                        Mem<Temp> mem = Mem.of(base.getTemp(), (int) offset.getValue());
                        return Operand.mem(mem);
                    } else {
                        throw XicInternalException.runtime("Invalid IR generated for immutable mem.");
                    }

                // B + R * scale
                } else if (bop.right() instanceof IRBinOp) {

                    Operand base = bop.left().accept(this);
    
                    IRBinOp index = (IRBinOp) bop.right();

                    assert index.type() == OpType.MUL &&
                        index.left() instanceof IRTemp &&
                        index.right() instanceof IRConst;
                        
                    Operand reg = index.left().accept(this);
                    Imm scale = checkImm(index.right()).get();

                    // Assumes no nested memory access
                    assert base.isTemp() && reg.isTemp();
                    
                    Mem<Temp> mem = Mem.of(
                        base.getTemp(),
                        reg.getTemp(),
                        (int) scale.getValue(),
                        0
                    );

                    return Operand.mem(mem);
                }
            }
        }

        Optional<Imm> imm = checkImm(m.expr());

        Temp t = TempFactory.generate("mem");
        instrs.addAll(mov(m.expr().accept(this), Operand.temp(t), imm));
        return Operand.mem(Mem.of(t));
    }

    public Operand visit(IRMove m) {

        // Source is global memory or global address
        if (m.src() instanceof IRMem) {
            IRMem mem = (IRMem) m.src();
            if (mem.isGlobal()) {
                Operand dest = m.target().accept(this);

                // LAR
                if (mem.expr instanceof IRMem && ((IRMem) mem.expr).isGlobal()) {
                    String src = ((IRTemp) ((IRMem) mem.expr).expr).name();
                    instrs.add(movLAR(src, dest.getTemp()));
                
                // LR
                } else {
                    String src = ((IRTemp) mem.expr).name();
                    instrs.add(movLR(src, dest.getTemp()));
                }

                return null;
            }
        }

        // Target is global memory
        if (m.target() instanceof IRMem) {
            IRMem mem = (IRMem) m.target();
            if (mem.isGlobal()) {
                Operand src = m.src().accept(this);
                String dest = ((IRTemp) mem.expr).name();
                instrs.add(movRL(src.getTemp(), dest));
                return null;
            }
        }

        // Ordinary move

        // Dest must be Temp or Mem<Temp>
        Operand dest = m.target().accept(this);

        // Src can be Imm
        Optional<Imm> imm = checkImm(m.src());
        Operand src = m.src().accept(this);

        instrs.addAll(mov(src, dest, imm));
        return null;
    }

    public Operand visit(IRName n) {
        throw XicInternalException.runtime("IRName should not be visited during tiling.");
    }

    public Operand visit(IRReturn r) {
        // CALLEE returns (write by callee)
        for (int i = r.size() - 1; i >= 0; i--) {
            Optional<Imm> imm = checkImm(r.get(i));
            instrs.addAll(
                mov(
                    r.get(i).accept(this),
                    Config.calleeRet(calleeReturnAddress, i),
                    imm
                )
            );
        }

        // Restore callee saved registers
        instrs.add(movRR(new Temp("_STORE_RBX"), Temp.RBX));
        instrs.add(movRR(new Temp("_STORE_R12"), Temp.R12));
        instrs.add(movRR(new Temp("_STORE_R13"), Temp.R13));
        instrs.add(movRR(new Temp("_STORE_R14"), Temp.R14));
        instrs.add(movRR(new Temp("_STORE_R15"), Temp.R15));
        
        instrs.add(jmpFromLabel(returnLabel));
        return null;
    }

    public Operand visit(IRSeq s) { 
        int i = 0;
        for(IRNode stmt : s.stmts()) {
            if (INCLUDE_COMMENTS) {
                String ir = "\nStmt " + i + ": " + stmt;
                ir = ir.replaceAll("\n\\s*", "\n# ");
                ir = ir.substring(0, ir.length() - 3);
                instrs.add(comment(ir));
            }
            stmt.accept(this);
            i++;
        }
        return null;
    }

    public Operand visit(IRTemp t) {
        String name = t.name();

        // CALLEE args (read by callee)
        if (name.matches(Configuration.ABSTRACT_ARG_PREFIX + "\\d+")) {
            // isMultiple defined by FUNCDECL to offset for multiple 
            // return address passed as arg0
            int num = Integer.parseInt(name.substring(4));
            return Config.calleeArg(num);
        } 

        // CALLER returns (read by caller)
        if (name.matches(Configuration.ABSTRACT_RET_PREFIX + "\\d+")) {
            // callerNumArgs defined by CALL
            int num = Integer.parseInt(name.substring(4));
            return Config.callerRet(num, callerNumArgs);
        }

        // Set calleeReturnAddress to CALLEE_MULT_RET
        if (t.equals(Library.CALLEE_MULT_RET)) {
            if (calleeReturnAddress == null) {
                calleeReturnAddress = new Temp(name);
            }
            return Operand.temp(calleeReturnAddress);
        }

        // Default temp
        return Operand.temp(new Temp(name));
    }
}
