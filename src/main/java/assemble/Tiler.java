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
import interpret.Configuration;
import ir.*;
import ir.IRBinOp.OpType;
import ir.IRMem.MemType;
import xic.XicInternalException;

import util.Pair;

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

    // 1 if current function has multiple returns else 0
    private int calleeIsMultiple;

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
        this.calleeIsMultiple = 0;
        this.callerNumArgs = 0;
        this.returnLabel = null;
    }

    /**
     * Returns number of return values for a function.
     * Takes the mangled function name.
     */
    private int numArgs(String fn) {
        if (fn.equals(Config.XI_ALLOC)) {
            return 1;
        } else if (fn.equals(Config.XI_OUT_OF_BOUNDS)) {
            return 0;
        }
        return context.getNumArgs(fn);
    }
    
    /**
     * Returns number of return values for a function.
     * Takes the mangled function name.
     */
    private int numReturns(String fn) {
        if (fn.equals(Config.XI_ALLOC)) {
            return 1;
        } else if (fn.equals(Config.XI_OUT_OF_BOUNDS)) {
            return 0;
        }
        return context.getNumReturns(fn);
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
     * Psuedo-visit method for visiting a list of nodes.
     */
    public List<Operand> visit(List<IRNode> nodes) {
        List<Operand> t = new ArrayList<>();
        for (IRNode n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }

    /*
     * Visitor methods ---------------------------------------------------------------------
     */
    
    public Operand visit(IRCompUnit c) {
        for (IRFuncDecl fn : c.functions().values()) {
            fn.accept(this);
        }
        return null;
    }

    public Operand visit(IRFuncDecl f) {

        // Reset instance variables for each function
        instrs = new ArrayList<>();
        String funcName = f.name();

        // Set the number of returns
        int returns = numReturns(funcName);

        // If function has multiple returns, save return address from arg 0 to a temp
        if (returns > 2) {
            calleeIsMultiple = 1;
            Temp returnAddr = Config.calleeArg(0).getTemp();
            Temp returnTemp = TempFactory.generate("RETURN");
            instrs.add(0, movRR(returnAddr, returnTemp));
        } else {
            calleeIsMultiple = 0;
            calleeReturnAddress = null;
        }

        // Set number of arguments including offset for multiple returns
        int args = numArgs(funcName) + calleeIsMultiple;

        // Set return label
        returnLabel = labelFromRet(f);

        // Tile the function body
        f.body().accept(this);


        // Set up prologue and epilogue
        FuncDecl.T fn = new FuncDecl.T(f, args, returns, instrs);
        unit.fns.add(fn);
        
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
        instrs.addAll(cmp(left, right));
        // TODO: this is sub-optimal use of setcc which can use other registers
        instrs.add(movIR(new Imm(0), Temp.RAX));
        instrs.add(setcc(flag));
        Temp t = TempFactory.generate();
        instrs.add(movRR(Temp.RAX, t));
        return Operand.temp(t);
    }
    
    public Operand visit(IRCall c) {
        String target = c.target().name();

        int callIsMultiple = 0;
        
        int numRets = numReturns(target);

        callerNumArgs = numArgs(target);

        // Set up for multiple returns from call
        if (numRets > 2) {
            callIsMultiple = 1;
            callerNumArgs++;

            // CALLER defines address that is passed as CALLER_RET_ADDR
            // Address passed is the same as address to write ret2 to

            // These unwraps are guaranteed to be safe based on our stack layout
            Temp callerArg = Config.callerArg(0).getTemp();
            Mem<Temp> callerRet = Config.callerRet(2, callerNumArgs).getMem();
            instrs.add(lea(callerRet, callerArg));
        }

        // CALLER args
        // callIsMultiple deined by this CALL
        for (int i = 0; i < c.size(); i++) {

            Optional<Imm> imm = checkImm(c.get(i)); 
            Operand arg = Config.callerArg(i + calleeIsMultiple);

            // Constant argument into register argument
            if (imm.isPresent() && arg.isTemp()) {
                instrs.add(movIR(imm.get(), arg.getTemp()));
            }
            
            // Constant argument into the stack for arguments
            else if (imm.isPresent() && arg.isMem()) {
                instrs.add(movIM(imm.get(), arg.getMem()));
            }

            // Non-constant argument into something
            else {
                Operand val = c.get(i).accept(this);
                instrs.addAll(mov(Config.callerArg(i + callIsMultiple), val));
            }
        }

        instrs.add(call(target, callerNumArgs, numRets));
        return Config.callerRet(0, callerNumArgs);
    }

    public Operand visit(IRCJump c) {

        if (c.cond instanceof IRBinOp) {
            IRBinOp bop = (IRBinOp) c.cond;

            Optional<Imm> immL = checkImm(bop.left());
            Optional<Imm> immR = checkImm(bop.right());

            // Both immediates; try to shuttle via registers
            if (immL.isPresent() && immR.isPresent()) {

                instrs.addAll(cmpII(immL.get(), immR.get()));

            } else if (immR.isPresent()) {

                Operand left = bop.left().accept(this);
                
                // Check if fits in imm32 for cmp instruction
                if (Config.within(32, immR.get().getValue())) {

                    // Check what the LHS side is: either temp or mem
                    if (left.isTemp()) {
                        instrs.add(cmpIR(immR.get(), left.getTemp()));
                    } else {
                    
                        //Otherwise must shuttle
                        Temp shuttle = TempFactory.generate("immR_mem_shuttle");
                        instrs.add(movIR(immR.get(), shuttle));
                        instrs.add(cmpRM(shuttle, left.getMem()));
                    }

                } else {
                    
                    //Otherwise shuttle
                    Temp shuttle = TempFactory.generate("immR_shuttle");
                    instrs.add(movIR(immR.get(), shuttle));

                    // Check what the LHS side is
                    if (left.isTemp()) {
                        instrs.add(cmpRR(shuttle, left.getTemp()));
                    } else {
                        instrs.add(cmpRM(shuttle, left.getMem()));
                    }
                }

            } else if (immL.isPresent()) {
                
                Operand right = bop.right().accept(this);

                // Have to shuttle since only IR addressing, no RI
                Temp shuttle = TempFactory.generate("immL_shuttle");
                instrs.add(movIR(immL.get(), shuttle));

                if (right.isTemp()) {
                    instrs.add(cmpRR(right.getTemp(), shuttle));
                } else {
                    instrs.add(cmpMR(right.getMem(), shuttle));
                }
            }

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
                    throw XicInternalException.runtime("Invalid binop for CJUMP");
            }
            instrs.add(jcc(flag, c.trueLabel()));
            return null;
        }

        Optional<Imm> imm = checkImm(c.cond);

        // Immediate condition
        if (imm.isPresent()) {
            instrs.addAll(cmpII(new Imm(1), imm.get()));
            instrs.add(jcc(Jcc.Kind.Z, c.trueLabel()));
            return null;
        }

        // Otherwise is either a temp or mem
        Operand cond = c.cond.accept(this);

        if (cond.isTemp()) {
            instrs.add(cmpIR(new Imm(1), cond.getTemp()));
        } else {
            // Must shuttle due to addressing modes for cmp
            Temp shuttle = TempFactory.generate("cond_shuttle");
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
        throw XicInternalException.runtime("IRESeq is not canonical");
    }

    public Operand visit(IRExp e) {
        // TODO: add tile to deal with procedure calls with no returns
        // in the form of IRExp(IRCall(...))
        throw XicInternalException.runtime("IRExp is not canonical");        
    }

    public Operand visit(IRLabel l) {
        instrs.add(labelFromIRLabel(l));
        return null;
    }

    public Operand visit(IRMem m) {
        // Use set temporaries to make allocator use addressing modes 
        // for immutable memory accesses
        if (m.memType() == MemType.IMMUTABLE && m.expr() instanceof IRBinOp) {

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
                        throw XicInternalException.runtime("Nested memory access");
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
                        0,
                        (int) scale.getValue()
                    );

                    return Operand.mem(mem);
                }
            }
        }

        Temp t = TempFactory.generate();
        instrs.addAll(mov(m.expr().accept(this), Operand.temp(t)));
        return Operand.mem(Mem.of(t));
    }

    // TODO: currently assumes the dest of an IRMove is never a constant

    // TODO: we are getting an assertion error here!
    public Operand visit(IRMove m) {

        // Must be Mem<Temp> or else IRGen has a bug
        Operand dest = m.target().accept(this);
            

        Optional<Imm> imm = checkImm(m.src());

        if (imm.isPresent()) {
            if (dest.isTemp()) {
                instrs.add(movIR(imm.get(), dest.getTemp()));
            } else {
                instrs.add(movIM(imm.get(), dest.getMem()));
            }
            return null;
        }

        Operand src = m.src().accept(this);
        instrs.addAll(mov(src, dest));
        return null;
    }

    public Operand visit(IRName n) {
        throw XicInternalException.runtime("IRName not visited");
    }

    public Operand visit(IRReturn r) {
        // CALLEE returns (write by callee)
        for (int i = r.size() - 1; i >= 0; i--) {

            Optional<Imm> imm = checkImm(r.get(i));

            // Constant return
            if (imm.isPresent()) {
                
                Operand ret = Config.calleeRet(calleeReturnAddress, i);
                
                // Check if return is a register or on the stack
                if (ret.isTemp()) {
                    instrs.add(movIR(imm.get(), ret.getTemp()));
                } else {
                    instrs.add(movIM(imm.get(), ret.getMem()));
                }

            }
            
            // Otherwise temp or mem
            else {
                instrs.addAll(
                    mov(
                        r.get(i).accept(this),
                        Config.calleeRet(calleeReturnAddress, i)
                    )
                );
            }
        }

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
            int num = Integer.parseInt(name.substring(4)) + calleeIsMultiple;
            return Config.calleeArg(num);
        } 

        // CALLER returns (read by caller)
        if (name.matches(Configuration.ABSTRACT_RET_PREFIX + "\\d+")) {
            // callerNumArgs defined by CALL
            int num = Integer.parseInt(name.substring(4));
            return Config.callerRet(num, callerNumArgs);
        }

        // Default temp
        return Operand.temp(new Temp(name));
    }
}
