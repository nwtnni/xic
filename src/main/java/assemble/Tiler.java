package assemble;

import java.util.List;
import java.util.ArrayList;

import assemble.instructions.*;
import assemble.Config;
import emit.ABIContext;
import ir.*;

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

    // Current function visited
    String funcName;

    // Current list of instructions
    List<Instr> instrs;

    private Tiler(ABIContext c) {
        this.context = c;
        this.unit = new CompUnit();
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
        funcName = f.name;
        instrs = new ArrayList<>();

        // Argument movement is handled in the body
        f.body.accept(this);

        unit.fns.add(new FuncDecl(f.name, instrs));
        return null;
    }

    public Temp visit(IRSeq s) {
        return null;
    }

    public Temp visit(IRESeq e) {
        return null;
    }

    public Temp visit(IRExp e) {
        return null;
    }

    public Temp visit(IRCall c) {
        return null;
    }

    public Temp visit(IRReturn r) {
        return null;
    }

    public Temp visit(IRCJump c) {
        return null;
    }

    public Temp visit(IRJump j) {
        return null;
    }
    
    public Temp visit(IRName n) {
        return null;
    }

    public Temp visit(IRLabel l) {
        return null;
    }

    public Temp visit(IRTemp t) {
        return null;
    }
    
    public Temp visit(IRMem m) {
        return null;
    }

    public Temp visit(IRMove m) {
        return null;
    }

    public Temp visit(IRBinOp b) {
        return null;
    }
    
    public Temp visit(IRConst c) {
        return null;
    }
    
}
