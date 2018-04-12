package assemble.tiles;

import java.util.List;
import java.util.ArrayList;

import ir.*;
import assemble.*;

public class FuncDecl extends Tile {
    public List<Tile> prelude;
    public List<Tile> stmts;
    public List<Tile> epilogue;
    
    public FuncDecl(List<Tile> prelude, List<Tile> stmts, List<Tile> epilogue) {
        this.prelude = prelude;
        this.stmts = stmts;
        this.epilogue = epilogue;
    }

    /**
     * Takes an IR tree and returns true if this tile can
     * cover a portion of the tree beginning at the root.
     */
    public static boolean matches(IRNode n) {
        return n instanceof IRFuncDecl;
    }

    /**
     * Traverses an IR tree and returns the list of untraversed 
     * children that are not covered by this tile. The order of the
     * children is the same as the order of tiles in children.
     */
    public static List<IRNode> traverse(IRNode n) {
        return null;
    }

    @Override
    public <T> T accept(TileVisitor<T> v) {
        return v.visit(this);
    }
}