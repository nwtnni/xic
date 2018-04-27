package assemble;

import java.util.List;

import java.util.ArrayList;

import static assemble.instructions.BinOp.Kind.*;
import static assemble.instructions.DivMul.Kind.*;
import static assemble.instructions.Setcc.Kind.*;

import assemble.instructions.*;
import assemble.Config;
import emit.ABIContext;
import interpret.Configuration;
import ir.*;
import ir.IRBinOp.OpType;
import ir.IRMem.MemType;
import xic.XicInternalException;

public class Tiler extends IRVisitor<Temp> {

    /**
     * Returns the list of abstract assembly code given an canonical IR tree
     */
    public static CompUnit tile(IRNode t, ABIContext c) {
        TempFactory.reset();
        Tiler tiler = new Tiler(c);
        t.accept(tiler);
        return tiler.unit;
    }

    public static final boolean INCLUDE_COMMENTS = false;

    // Mangled names context
    private ABIContext context;

    // Running list of assembly instructions
    private CompUnit unit;

    // Current list of instructions
    List<Instr> instrs;

    // 1 if current function has multiple returns else 0
    private int calleeIsMultiple;

    // Number of args passed to a called function
    private int callerNumArgs;

    // Return label of current function visited
    Label returnLabel;

    private Tiler(ABIContext c) {
        this.context = c;
        this.unit = new CompUnit();

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

    /*
     * Psuedo-visit method for visiting a list of nodes.
     */
    public List<Temp> visit(List<IRNode> nodes) {
        List<Temp> t = new ArrayList<>();
        for (IRNode n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }

    /*
     * Visitor methods ---------------------------------------------------------------------
     */
    
    public Temp visit(IRCompUnit c) {
        for (IRFuncDecl fn : c.functions().values()) {
            fn.accept(this);
        }
        return null;
    }

    public Temp visit(IRFuncDecl f) {
        // Reset instance variables for each function
        instrs = new ArrayList<>();
        TempFactory.reset();

        String funcName = f.name();

        // Set the number of returns
        int returns = numReturns(funcName);

        // If function has multiple returns, save return address from arg 0 to a temp
        if (returns > 2) {
            calleeIsMultiple = 1;
            instrs.add(0, new Mov(Temp.CALLEE_RET_ADDR, Temp.calleeArg(0)));
        } else {
            calleeIsMultiple = 0;
        }

        // Set number of arguments including offset for multiple returns
        int args = numArgs(funcName) + calleeIsMultiple;

        // Set return label
        returnLabel = Label.retLabel(f);

        // Tile the function body
        f.body().accept(this);


        // Set up prologue and epilogue
        FuncDecl fn = new FuncDecl(f, args, returns, instrs);
        unit.fns.add(fn);
        
        return null;
    }

    public Temp visit(IRBinOp b) {
        Temp dest = TempFactory.generate();
        Temp left = b.left().accept(this);
        Temp right = b.right().accept(this);
        
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
            case LSHIFT:
                bop = LSHIFT;
                break;
            case RSHIFT:
                bop = RSHIFT;
                break;
            case ARSHIFT:
                bop = ARSHIFT;
                break;
            default:
        }
        if (bop != null) {
            instrs.add(new Mov(dest, left));
            instrs.add(new BinOp(bop, dest, right));
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
            instrs.add(new Mov(Temp.RAX, left));
            if (uop == DIV || uop == MOD) {
                instrs.add(new Cqo());
            }
            DivMul op = new DivMul(uop, right);
            instrs.add(op);
            instrs.add(new Mov(dest, op.destTemp));
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
        instrs.add(new Cmp(right, left));
        // TODO: this is sub-optimal use of setcc which can use other registers
        instrs.add(new Mov(Temp.RAX, Temp.imm(0)));
        instrs.add(new Setcc(flag));
        instrs.add(new Mov(dest, Temp.RAX));
        return dest;
    }
    
    public Temp visit(IRCall c) {
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
            instrs.add(new Lea(Temp.callerArg(0), Temp.callerRet(2, callerNumArgs)));
        }

        // CALLER args
        // callIsMultiple deined by this CALL
        for (int i = 0; i < c.size(); i++) {
            Temp val = c.get(i).accept(this);
            instrs.add(new Mov(Temp.callerArg(i + callIsMultiple), val));
        }

        instrs.add(new Call(target, callerNumArgs, numRets));
        return Temp.callerRet(0, callerNumArgs);
    }

    public Temp visit(IRCJump c) {
        if (c.cond instanceof IRBinOp) {
            IRBinOp bop = (IRBinOp) c.cond;
            Temp left = bop.left().accept(this);
            Temp right = bop.right().accept(this);
            instrs.add(new Cmp(right, left));
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
            instrs.add(new Jcc(flag, c.trueLabel()));
            return null;
        }

        Temp cond = c.cond.accept(this);
        instrs.add(new Cmp(Temp.imm(1), cond));
        instrs.add(new Jcc(Jcc.Kind.Z, c.trueLabel()));
        return null;
    }

    public Temp visit(IRConst c) {
        return Temp.imm(c.value());
    }

    public Temp visit(IRJump j) {
        instrs.add(Jmp.fromJmp(j));
        return null;
    }

    public Temp visit(IRESeq e) {
        throw XicInternalException.runtime("IRESeq is not canonical");
    }

    public Temp visit(IRExp e) {
        // TODO: add tile to deal with procedure calls with no returns
        // in the form of IRExp(IRCall(...))
        throw XicInternalException.runtime("IRExp is not canoncial");        
    }

    public Temp visit(IRLabel l) {
        instrs.add(Label.label(l));
        return null;
    }

    public Temp visit(IRMem m) {
        // Use set temporaries to make allocator use addressing modes 
        // for immutable memory accesses
        if (m.memType() == MemType.IMMUTABLE && m.expr() instanceof IRBinOp) {
            IRBinOp bop = (IRBinOp) m.expr();
            assert bop.type() == OpType.ADD;
            if (bop.left() instanceof IRTemp) { 
                // B + off
                if (bop.right() instanceof IRConst) {
                    Temp base = bop.left().accept(this);
                    Temp offset = bop.right().accept(this);

                    // off must be within 32 bits
                    assert Config.within(32, offset.value);

                    return Temp.mem(base, (int) offset.value);

                // B + R * scale
                } else if (bop.right() instanceof IRBinOp) {
                    Temp base = bop.left().accept(this);
    
                    IRBinOp index = (IRBinOp) bop.right();
                    assert index.type() == OpType.MUL &&
                        index.left() instanceof IRTemp &&
                        index.right() instanceof IRConst;
                        
                    Temp reg = index.left().accept(this);
                    Temp scale = index.right().accept(this);
                    
                    return Temp.mem(base, reg, 0, (int) scale.value);
                }
            }
        }

        Temp t = TempFactory.generate();
        instrs.add(new Mov(t, m.expr().accept(this)));
        return Temp.mem(t);
    }

    public Temp visit(IRMove m) {
        Temp src = m.src().accept(this);
        Temp dest = m.target().accept(this);
        instrs.add(new Mov(dest, src));
        return null;
    }

    public Temp visit(IRName n) {
        throw XicInternalException.runtime("IRName not visited");
    }

    public Temp visit(IRReturn r) {
        // CALLEE returns (write by callee)
        for (int i = r.size() - 1; i >= 0; i--) {
            Temp val = r.get(i).accept(this);
            instrs.add(new Mov(Temp.calleeRet(i), val));
        }
        instrs.add(Jmp.toLabel(returnLabel));
        return null;
    }

    public Temp visit(IRSeq s) { 
        int i = 0;
        for(IRNode stmt : s.stmts()) {
            if (INCLUDE_COMMENTS) {
                String ir = "\nStmt " + i + ": " + stmt;
                ir = ir.replaceAll("\n\\s*", "\n# ");
                ir = ir.substring(0, ir.length() - 3);
                instrs.add(Text.comment(ir));
            }
            stmt.accept(this);
            i++;
        }
        return null;
    }

    public Temp visit(IRTemp t) {
        String name = t.name();

        // CALLEE args (read by callee)
        if (name.matches(Configuration.ABSTRACT_ARG_PREFIX + "\\d+")) {
            // isMultiple defined by FUNCDECL to offset for multiple 
            // return address passed as arg0
            int num = Integer.parseInt(name.substring(4)) + calleeIsMultiple;
            return Temp.calleeArg(num);
        } 

        // CALLER returns (read by caller)
        if (name.matches(Configuration.ABSTRACT_RET_PREFIX + "\\d+")) {
            // callerNumArgs defined by CALL
            int num = Integer.parseInt(name.substring(4));
            return Temp.callerRet(num, callerNumArgs);
        }

        // Default temp
        return Temp.temp(name);
    }
}
