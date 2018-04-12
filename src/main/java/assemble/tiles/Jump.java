package assemble.tiles;

import java.util.List;
import java.util.Arrays;

import ir.*;
import assemble.*;

public class Jump extends Tile {

    public Tile target;

    public Jump(Tile target) {
        this.target = target;
    }

    /**
     * Takes an IR tree and returns true if this tile can
     * cover a portion of the tree beginning at the root.
     */
    public static boolean matches(IRNode n) {
        return n instanceof IRJump;
    }

    /**
     * Traverses an IR tree and returns the list of untraversed 
     * children that are not covered by this tile. The order of the
     * children is the same as the order of tiles in children.
     */
    public static List<IRNode> traverse(IRNode n) {
        IRJump j = (IRJump) n;
        return Arrays.asList(j.target);
    }

    @Override
    public <T> T accept(TileVisitor<T> v) {
        return v.visit(this);
    }
}