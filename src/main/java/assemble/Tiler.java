package assemble;

import java.util.List;
import java.util.ArrayList;

import static assemble.instructions.BinOp.Kind.*;
import static assemble.instructions.BinMul.Kind.*;
import static assemble.instructions.BinCmp.Kind.*;

import assemble.instructions.*;
import assemble.Config;
import emit.ABIContext;
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
        
        switch (b.type) {
            case ADD:
                instrs.add(new BinOp(ADD, dest, left, right));
                return dest;
            case SUB:
                instrs.add(new BinOp(SUB, dest, left, right));
                return dest;
            case AND:
                instrs.add(new BinOp(SUB, dest, left, right));
                return dest;
            case OR:
                instrs.add(new BinOp(OR, dest, left, right));
                return dest;
            case XOR:
                instrs.add(new BinOp(XOR, dest, left, right));
                return dest;
            case LSHIFT:
                instrs.add(new BinOp(LSHIFT, dest, left, right));
                return dest;
            case RSHIFT:
                instrs.add(new BinOp(RSHIFT, dest, left, right));
                return dest;
            case ARSHIFT:
                instrs.add(new BinOp(ARSHIFT, dest, left, right));
                return dest;
            case MUL:
                instrs.add(new BinMul(MUL, dest, left, right));
                return dest;
            case HMUL:
                instrs.add(new BinMul(HMUL, dest, left, right));
                return dest;
            case DIV:
                instrs.add(new BinMul(DIV, dest, left, right));
                return dest;
            case MOD:
                instrs.add(new BinMul(MOD, dest, left, right));
                return dest;
            case EQ:
                instrs.add(new BinCmp(EQ, dest, left, right));
                return dest;
            case NEQ:
                instrs.add(new BinCmp(NEQ, dest, left, right));
                return dest;
            case LT:
                instrs.add(new BinCmp(LT, dest, left, right));
                return dest;
            case GT:
                instrs.add(new BinCmp(GT, dest, left, right));
                return dest;
            case LEQ:
                instrs.add(new BinCmp(LEQ, dest, left, right));
                return dest;
            case GEQ:
                instrs.add(new BinCmp(GEQ, dest, left, right));
                return dest;
        }

        // These cases should be exhaustive
        assert false;
        return null;
    }
    
    public Temp visit(IRCall c) {
        List<Instr> args = new ArrayList<>();

        String target = ((IRName) c.target).name();

        int callIsMultiple = 0;
        if (numReturns(target) > 2) {
            callIsMultiple = 1;
        }

        // TODO: separating the moves from calculating temps to movs
        // with separate instruction list in call might be bad

        // Assign multiple return address to argument 0 if needed
        // TODO: handle replacement with actual memory address in reg alloc
        if (callIsMultiple > 0) {
            args.add(new Lea(TempFactory.getArgument(0), Config.CALLER_MULT_RETURN));
        }

        // Assign all arguments into abstract argument registers
        // TODO: handle spilling and allocate lower 6 args into regs in reg alloc
        for (int i = 0; i < c.args.size(); i++) {
            Temp val = c.args.get(i).accept(this);
            args.add(new Mov(TempFactory.getArgument(i + callIsMultiple), val));
        }

        instrs.add(new Call(target, args));
        return TempFactory.getReturn(0);
    }

    public Temp visit(IRCJump c) {
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
        Temp t = TempFactory.generate(m.toString());
        instrs.add(new Mov(t, m.expr.accept(this)));
        return t;
    }

    public Temp visit(IRMove m) {
        Temp dest = m.target.accept(this);
        Temp src = m.src.accept(this);
        instrs.add(new Mov(dest, src));
        return null;
    }

    public Temp visit(IRName n) {
        throw XicInternalException.internal("IRName not visited");
    }

    public Temp visit(IRReturn r) {
        for (int i = 0; i < r.rets.size(); i++) {
            Temp val = r.rets.get(i).accept(this);
            instrs.add(new Mov(TempFactory.getReturn(i), val));
        }
        instrs.add(new Jmp(Label.retLabel(funcName).toString()));
        return null;
    }

    public Temp visit(IRSeq s) { 
        int i = 0;
        for(IRNode stmt : s.stmts) {
            instrs.add(Text.comment("stmt: " + i));
            stmt.accept(this);
            i++;
        }
        return null;
    }

    public Temp visit(IRTemp t) {
        return Temp.temp(t.name());
    }
}
