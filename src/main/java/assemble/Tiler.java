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

import util.Either;

public class Tiler extends IRVisitor<Either<Temp, Mem<Temp>>> {

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
    public List<Either<Temp, Mem<Temp>>> visit(List<IRNode> nodes) {
        List<Either<Temp, Mem<Temp>>> t = new ArrayList<>();
        for (IRNode n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }

    /*
     * Visitor methods ---------------------------------------------------------------------
     */
    
    public Either<Temp, Mem<Temp>> visit(IRCompUnit c) {
        for (IRFuncDecl fn : c.functions().values()) {
            fn.accept(this);
        }
        return null;
    }

    public Either<Temp, Mem<Temp>> visit(IRFuncDecl f) {

        // Reset instance variables for each function
        instrs = new ArrayList<>();
        String funcName = f.name();

        // Set the number of returns
        int returns = numReturns(funcName);

        // If function has multiple returns, save return address from arg 0 to a temp
        if (returns > 2) {
            calleeIsMultiple = 1;
            Temp returnAddr = Config.calleeArg(0).getLeft();
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

    public Either<Temp, Mem<Temp>> visit(IRBinOp b) {
        Either<Temp, Mem<Temp>> dest = Either.left(TempFactory.generate());

        Optional<Imm> immL = checkImm(b.left());
        Optional<Imm> immR = checkImm(b.right());

        Either<Temp, Mem<Temp>> left = b.left().accept(this);
        Either<Temp, Mem<Temp>> right = b.right().accept(this);
        
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
            instrs.addAll(mov(left, dest));
            instrs.addAll(binOp(bop, dest, right));
            return dest;
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
            instrs.addAll(mov(left, Either.left(Temp.RAX)));

            if (uop == DIV || uop == MOD) instrs.add(cqo());

            if (right.isLeft()) {
                DivMul<Temp, Temp> op = divMulR(uop, right.getLeft());
                instrs.add(op);
                instrs.add(movRR(op.dest, dest.getLeft()));
            } else {
                DivMul<Mem<Temp>, Temp> op = divMulM(uop, right.getRight());
                instrs.add(op);
                instrs.add(movRR(op.dest, dest.getLeft()));
            }

            return dest;
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
        instrs.add(movRR(Temp.RAX, dest.getLeft()));
        return dest;
    }
    
    public Either<Temp, Mem<Temp>> visit(IRCall c) {
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
            Temp callerArg = Config.callerArg(0).getLeft();
            Mem<Temp> callerRet = Config.callerRet(2, callerNumArgs).getRight();
            instrs.add(lea(callerRet, callerArg));
        }

        // CALLER args
        // callIsMultiple deined by this CALL
        for (int i = 0; i < c.size(); i++) {

            Optional<Imm> imm = checkImm(c.get(i)); 
            Either<Temp, Mem<Temp>> arg = Config.callerArg(i + calleeIsMultiple);

            // Constant argument into register
            if (imm.isPresent() && arg.isLeft()) {
                instrs.add(movIR(imm.get(), arg.getLeft()));
            }
            
            // Constant argument into mem
            else if (imm.isPresent() && arg.isRight()) {
                instrs.add(movIM(imm.get(), arg.getRight()));
            }

            // Non-constant argument into something
            else {
                Either<Temp, Mem<Temp>> val = c.get(i).accept(this);
                instrs.addAll(mov(Config.callerArg(i + callIsMultiple), val));
            }
        }

        instrs.add(call(target, callerNumArgs, numRets));
        return Config.callerRet(0, callerNumArgs);
    }

    public Either<Temp, Mem<Temp>> visit(IRCJump c) {

        if (c.cond instanceof IRBinOp) {
            IRBinOp bop = (IRBinOp) c.cond;

            Optional<Imm> immL = checkImm(bop.left());
            Optional<Imm> immR = checkImm(bop.right());

            // Both immediates; try to shuttle via registers
            if (immL.isPresent() && immR.isPresent()) {

                instrs.addAll(cmpII(immL.get(), immR.get()));

            } else if (immR.isPresent()) {

                Either<Temp, Mem<Temp>> left = bop.left().accept(this);
                
                // Check if fits in imm32 for cmp instruction
                if (Config.within(32, immR.get().getValue())) {

                    // Check what the LHS side is: either temp or mem
                    if (left.isLeft()) {
                        instrs.add(cmpIR(immR.get(), left.getLeft()));
                    } else {
                    
                        //Otherwise must shuttle
                        Temp shuttle = TempFactory.generate("immR_mem_shuttle");
                        instrs.add(movIR(immR.get(), shuttle));
                        instrs.add(cmpRM(shuttle, left.getRight()));
                    }

                } else {
                    
                    //Otherwise shuttle
                    Temp shuttle = TempFactory.generate("immR_shuttle");
                    instrs.add(movIR(immR.get(), shuttle));

                    // Check what the LHS side is
                    if (left.isLeft()) {
                        instrs.add(cmpRR(shuttle, left.getLeft()));
                    } else {
                        instrs.add(cmpRM(shuttle, left.getRight()));
                    }
                }

            } else if (immL.isPresent()) {
                
                Either<Temp, Mem<Temp>> right = bop.right().accept(this);

                // Have to shuttle since only IR addressing, no RI
                Temp shuttle = TempFactory.generate("immL_shuttle");
                instrs.add(movIR(immL.get(), shuttle));

                if (right.isLeft()) {
                    instrs.add(cmpRR(right.getLeft(), shuttle));
                } else {
                    instrs.add(cmpMR(right.getRight(), shuttle));
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
        Either<Temp, Mem<Temp>> cond = c.cond.accept(this);

        if (cond.isLeft()) {
            instrs.add(cmpIR(new Imm(1), cond.getLeft()));
        } else {
            // Must shuttle due to addressing modes for cmp
            Temp shuttle = TempFactory.generate("cond_shuttle");
            instrs.add(movIR(new Imm(1), shuttle));
            instrs.add(cmpRM(shuttle, cond.getRight()));
        }

        instrs.add(jcc(Jcc.Kind.Z, c.trueLabel()));
        return null;
    }

    public Either<Temp, Mem<Temp>> visit(IRConst c) {
        throw XicInternalException.runtime("Missed an immediate");
    }

    public Either<Temp, Mem<Temp>> visit(IRJump j) {
        instrs.add(jmpFromIRJump(j));
        return null;
    }

    public Either<Temp, Mem<Temp>> visit(IRESeq e) {
        throw XicInternalException.runtime("IRESeq is not canonical");
    }

    public Either<Temp, Mem<Temp>> visit(IRExp e) {
        // TODO: add tile to deal with procedure calls with no returns
        // in the form of IRExp(IRCall(...))
        throw XicInternalException.runtime("IRExp is not canonical");        
    }

    public Either<Temp, Mem<Temp>> visit(IRLabel l) {
        instrs.add(labelFromIRLabel(l));
        return null;
    }

    public Either<Temp, Mem<Temp>> visit(IRMem m) {
        // Use set temporaries to make allocator use addressing modes 
        // for immutable memory accesses
        if (m.memType() == MemType.IMMUTABLE && m.expr() instanceof IRBinOp) {

            IRBinOp bop = (IRBinOp) m.expr();
            assert bop.type() == OpType.ADD;

            if (bop.left() instanceof IRTemp) { 

                // B + off
                if (bop.right() instanceof IRConst) {

                    Either<Temp, Mem<Temp>> base = bop.left().accept(this);
                    Imm offset = checkImm(bop.right()).get();

                    // Off must be within 32 bits
                    assert Config.within(32, offset.getValue());

                    // B must be a temp, not nested memory access
                    if (base.isLeft()) {
                        Mem<Temp> mem = Mem.of(base.getLeft(), (int) offset.getValue());
                        return Either.right(mem);
                    } else {
                        throw XicInternalException.runtime("Nested memory access");
                    }

                // B + R * scale
                } else if (bop.right() instanceof IRBinOp) {

                    Either<Temp, Mem<Temp>> base = bop.left().accept(this);
    
                    IRBinOp index = (IRBinOp) bop.right();

                    assert index.type() == OpType.MUL &&
                        index.left() instanceof IRTemp &&
                        index.right() instanceof IRConst;
                        
                    Either<Temp, Mem<Temp>> reg = index.left().accept(this);
                    Imm scale = checkImm(index.right()).get();

                    // Assumes no nested memory access
                    assert base.isLeft() && reg.isLeft();
                    
                    Mem<Temp> mem = Mem.of(
                        base.getLeft(),
                        reg.getLeft(),
                        0,
                        (int) scale.getValue()
                    );

                    return Either.right(mem);
                }
            }
        }

        Temp t = TempFactory.generate();
        instrs.addAll(mov(m.expr().accept(this), Either.left(t)));
        return Either.right(Mem.of(t));
    }

    // TODO: currently assumes the dest of an IRMove is never a constant
    public Either<Temp, Mem<Temp>> visit(IRMove m) {

        // Must be Mem<Temp> or else IRGen has a bug
        Either<Temp, Mem<Temp>> dest = m.target().accept(this);
            

        Optional<Imm> imm = checkImm(m.src());

        if (imm.isPresent()) {
            if (dest.isLeft()) {
                instrs.add(movIR(imm.get(), dest.getLeft()));
            } else {
                instrs.add(movIM(imm.get(), dest.getRight()));
            }
            return null;
        }

        Either<Temp, Mem<Temp>> src = m.src().accept(this);
        instrs.addAll(mov(src, dest));
        return null;
    }

    public Either<Temp, Mem<Temp>> visit(IRName n) {
        throw XicInternalException.runtime("IRName not visited");
    }

    public Either<Temp, Mem<Temp>> visit(IRReturn r) {
        // CALLEE returns (write by callee)
        for (int i = r.size() - 1; i >= 0; i--) {

            Optional<Imm> imm = checkImm(r.get(i));

            // Constant return
            if (imm.isPresent()) {
                
                Either<Temp, Mem<Temp>> ret = Config.calleeRet(calleeReturnAddress, i);
                
                // Check if return is a register or on the stack
                if (ret.isLeft()) {
                    instrs.add(movIR(imm.get(), ret.getLeft()));
                } else {
                    instrs.add(movIM(imm.get(), ret.getRight()));
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

    public Either<Temp, Mem<Temp>> visit(IRSeq s) { 
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

    public Either<Temp, Mem<Temp>> visit(IRTemp t) {
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
        return Either.left(new Temp(name));
    }
}
