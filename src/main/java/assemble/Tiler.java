package assemble;

import java.util.List;
import java.util.ArrayList;

import static assemble.instructions.BinOp.Kind.*;
import static assemble.instructions.DivMul.Kind.*;
import static assemble.instructions.Set.Kind.*;

import assemble.instructions.*;
import assemble.Config;
import emit.ABIContext;
import interpret.Configuration;
import ir.*;
import xic.XicInternalException;

public class Tiler extends IRVisitor<Temp> {

    /**
     * Returns the list of abstract assembly code given an canonical IR tree
     */
    public static CompUnit tile(IRNode t, ABIContext c) {
        Tiler tiler = new Tiler(c);
        t.accept(tiler);
        return tiler.unit;
    }

    // Mangled names context
    private ABIContext context;

    // Running list of assembly instructions
    private CompUnit unit;

    // Current list of instructions
    List<Instr> instrs;

    // Current function visited
    String funcName;

    private Tiler(ABIContext c) {
        this.context = c;
        this.unit = new CompUnit();

        this.instrs = new ArrayList<>();
        this.funcName = null;
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
     * Visitor methods
     */
    
    public Temp visit(IRCompUnit c) {
        for (IRFuncDecl fn : c.functions.values()) {
            fn.accept(this);
        }
        return null;
    }

    public Temp visit(IRFuncDecl f) {
        // Reset instance variables for each function
        funcName = f.name();
        TempFactory.reset();

        int args = numArgs(funcName);
        int returns = numReturns(funcName);

        // Argument movement is handled in the body
        f.body.accept(this);

        unit.fns.add(new FuncDecl(funcName, args, returns, instrs));

        // Reset shared variables
        instrs = new ArrayList<>();
        return null;
    }

    public Temp visit(IRBinOp b) {
        // Uses %rax to operate on things. Returns %rax (sometimes %rdx)
        Temp dest = TempFactory.generate();
        Temp left = b.left.accept(this);
        Temp right = b.right.accept(this);
        
        BinOp.Kind bop = null;
        switch (b.type) {
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
        switch (b.type) {
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
            instrs.add(new Mov(Operand.RAX, left));
            if (uop == DIV || uop == MOD) {
                instrs.add(new Cqo());
            }
            DivMul op = new DivMul(uop, right);
            instrs.add(op);
            instrs.add(new Mov(dest, op.dest));
            return dest;
        }
            
        Set.Kind flag = null;
        switch (b.type) {
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
        instrs.add(new Mov(Operand.RAX, Operand.imm(0)));
        instrs.add(new Set(flag));
        instrs.add(new Mov(dest, Operand.RAX));
        return dest;
    }
    
    public Temp visit(IRCall c) {
        List<Instr> args = new ArrayList<>();

        String target = ((IRName) c.target).name();

        int callIsMultiple = 0;
        int ret = numReturns(target);
        if (ret > 2) {
            callIsMultiple = 1;
            args.add(new Lea(Temp.arg(0, false), Temp.MULT_RET_ADDR));
        }

        for (int i = 0; i < c.args.size(); i++) {
            Temp val = c.args.get(i).accept(this);
            args.add(new Mov(Temp.arg(i + callIsMultiple, false), val));
        }

        instrs.add(new Call(target, args, ret));
        return Temp.ret(0, false);
    }

    public Temp visit(IRCJump c) {
        if (c.cond instanceof IRBinOp) {
            IRBinOp bop = (IRBinOp) c.cond;
            Temp left = bop.left.accept(this);
            Temp right = bop.right.accept(this);
            instrs.add(new Cmp(right, left));
            Jcc.Kind flag = null;
            switch (bop.type) {
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
                    throw XicInternalException.internal("Invalid binop for CJUMP");
            }
            instrs.add(new Jcc(flag, c.trueLabel));
        }

        Temp cond = c.cond.accept(this);
        instrs.add(new Cmp(Temp.imm(1), cond));
        instrs.add(new Jcc(Jcc.Kind.Z, c.trueLabel));
        return null;
    }

    public Temp visit(IRJump j) {
        instrs.add(new Jmp(((IRName) j.target).name()));
        return null;
    }

    public Temp visit(IRConst c) {
        return Temp.imm(c.value);
    }

    public Temp visit(IRESeq e) {
        throw XicInternalException.internal("IRESeq is not canonical");
    }

    public Temp visit(IRExp e) {
        throw XicInternalException.internal("IRExp is not canoncial");        
    }

    public Temp visit(IRLabel l) {
        instrs.add(Label.label(l.name()));
        return null;
    }

    public Temp visit(IRMem m) {
        Temp t = TempFactory.generate();
        instrs.add(new Mov(t, m.expr.accept(this)));
        return Temp.mem(t.name);
    }

    public Temp visit(IRMove m) {
        Temp src = m.src.accept(this);
        Temp dest = m.target.accept(this);
        instrs.add(new Mov(dest, src));
        return null;
    }

    public Temp visit(IRName n) {
        throw XicInternalException.internal("IRName not visited");
    }

    public Temp visit(IRReturn r) {
        for (int i = r.rets.size() - 1; i >= 0; i--) {
            Temp val = r.rets.get(i).accept(this);
            instrs.add(new Mov(Temp.ret(i, true), val));
        }
        instrs.add(new Jmp(Label.retLabel(funcName).name()));
        return null;
    }

    public Temp visit(IRSeq s) { 
        int i = 0;
        for(IRNode stmt : s.stmts) {
            instrs.add(Text.comment("stmt: " + i));
            stmt.accept(this);
            instrs.add(Text.text(""));
            i++;
        }
        return null;
    }

    public Temp visit(IRTemp t) {
        String name = t.name();

        // Argument read by callee
        if (name.matches(Configuration.ABSTRACT_ARG_PREFIX + "\\d+")) {
            return Temp.arg(Integer.parseInt(name.substring(4)), true);
            
        } 
        
        // Return read by caller
        if (name.matches(Configuration.ABSTRACT_RET_PREFIX + "\\d+")) {
            return Temp.ret(Integer.parseInt(name.substring(4)), false);
        }

        // Default temp
        return Temp.temp(name);
    }
}
