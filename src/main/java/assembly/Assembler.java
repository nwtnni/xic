package assembly;

import ir.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler extends IRVisitor<Integer> {
    
    /**
     * Returns the assembly code given a canonical IR AST
     *
     */
    public static Integer assemble(IRNode ast) {
        Assembler assembler = new Assembler();
        ast.accept(assembler);
        return null;
    }
    
    /**
     * Internal utility function for debugging.
     *
     * Returns the current list of tiles 
     */
    public static List<String> debug(IRNode ast) {
        Assembler assembler = new Assembler();
        ast.accept(assembler);
        return assembler.cmds; 
    }

    /**
     * The running list of assembly commands
     */
    public List<String> cmds;

    /**
     * Constructor initializes @param cmds.
     */
    private Assembler() {
        cmds = new ArrayList<String>();
    }

    public Integer visit(IRBinOp b) {
        return null;
    }
    
    public Integer visit(IRCall c) {
        return null;
    }

    public Integer visit(IRCJump c) {
        return null;
    }

    public Integer visit(IRJump j) {
        return null;
    }
    
    public Integer visit(IRCompUnit c) {
        return null;
    }

    public Integer visit(IRConst c) {
        return null;
    }

    public Integer visit(IRESeq e) {
        return null;
    }

    public Integer visit(IRExp e) {
        return null;
    }

    public Integer visit(IRFuncDecl f) {
        return null;
    }

    public Integer visit(IRLabel l) {
        return null;
    }

    public Integer visit(IRMem m) {
        return null;
    }

    public Integer visit(IRMove m) {
        return null;
    }

    public Integer visit(IRName n) {
        return null;
    }

    public Integer visit(IRReturn r) {
        return null;
    }

    public Integer visit(IRSeq s) {
        return null;
    }

    public Integer visit(IRTemp t) {
        return null;
    }
}
