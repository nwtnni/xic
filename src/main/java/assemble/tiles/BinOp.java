package assemble.tiles;

import java.util.List;
import java.util.Arrays;

import ir.*;
import assemble.*;

public class BinOp extends Tile {
    public enum Kind {

    }

    public Kind kind;
    public Tile left;
    public Tile right;

    public BinOp(String destTemp, Tile left, Tile right) {
        this.destTemp = destTemp;
        this.left = left;
        this.right = right;
    }

    /**
     * Takes an IR tree and returns true if this tile can
     * cover a portion of the tree beginning at the root.
     */
    public static boolean matches(IRNode n) {
        return n instanceof IRBinOp;
    }

    /**
     * Traverses an IR tree and returns the list of untraversed 
     * children that are not covered by this tile. The order of the
     * children is the same as the order of tiles in children.
     */
    public static List<IRNode> traverse(IRNode n) {
        IRBinOp op = (IRBinOp) n;
        return Arrays.asList(op.left, op.right);
    }

    @Override
    public <T> T accept(TileVisitor<T> v) {
        return v.visit(this);
    }
}