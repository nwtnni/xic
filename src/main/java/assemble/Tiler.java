package assemble;

import assemble.tiles.*;
import ir.*;

import java.util.List;
import java.util.ArrayList;

public class Tiler extends IRVisitor<Tile> {

    /*
     * Psuedo-visit method for visiting a list of nodes.
     */
    public List<Tile> visit(List<IRNode> nodes) {
        List<Tile> t = new ArrayList<>();
        for (IRNode n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }
    
    public Tile visit(IRCompUnit c) {
        return null;
    }

    public Tile visit(IRFuncDecl f) {
        return null;
    }

    public Tile visit(IRSeq s) {
        return null;
    }

    public Tile visit(IRESeq e) {
        return null;
    }

    public Tile visit(IRExp e) {
        return null;
    }

    public Tile visit(IRCall c) {
        return null;
    }

    public Tile visit(IRReturn r) {
        return null;
    }

    public Tile visit(IRCJump c) {
        return null;
    }

    public Tile visit(IRJump j) {
        return null;
    }
    
    public Tile visit(IRName n) {
        return null;
    }

    public Tile visit(IRLabel l) {
        return null;
    }

    public Tile visit(IRTemp t) {
        return null;
    }
    
    public Tile visit(IRMem m) {
        return null;
    }

    public Tile visit(IRMove m) {
        return null;
    }

    public Tile visit(IRBinOp b) {
        return null;
    }
    
    public Tile visit(IRConst c) {
        return null;
    }
    
}
