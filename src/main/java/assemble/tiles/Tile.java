package assemble.tiles;

import java.util.List;

import ir.*;
import assemble.*;

public abstract class Tile {
    protected String destTemp;
    
    public Operand dest;
    public List<Operand> src;

    /**
     * Takes an IR tree and returns true if this tile can
     * cover a portion of the tree beginning at the root.
     */
    public static boolean matches(IRNode n) {
        return false;
    }

    /**
     * Traverses an IR tree and returns the list of untraversed 
     * children that are not covered by this tile. The order of the
     * children is the same as the order of tiles in children.
     */
    public static List<IRNode> traverse(IRNode n) {
        return null;
    };

    public abstract <T> T accept(TileVisitor<T> v);
}