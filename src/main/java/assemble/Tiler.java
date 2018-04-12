package assemble;

import assemble.instructions.*;
import emit.ABIContext;
import ir.*;

import java.util.List;
import java.util.ArrayList;

public class Tiler extends IRVisitor<Temp> {

    private int tempCounter = 0;                    // How many temps are used
    private int maxReturn = 0;                      // Amount of stack space for returns
    private int maxArgs = 0;                        // Amount of stack space for args
    private int isMultipleReturn = 0;                   // 1 if the function returns > 2 elements

    // Mangled names context
    private ABIContext context;

    // Running list of assembly instructions
    private List<Instr> instrs;

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
    
    public Temp visit(IRCompUnit c) {
        return null;
    }

    public Temp visit(IRFuncDecl f) {
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
