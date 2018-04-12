package assemble.tiles;

import java.util.List;
import java.util.Arrays;

import ir.*;
import assemble.*;

public class CJump extends Tile {

    public String target;
    public Tile cond;

    public CJump(String target, Tile cond) {
        this.target = target;
        this.cond = cond;
    }

    /**
     * Takes an IR tree and returns true if this tile can
     * cover a portion of the tree beginning at the root.
     */
    public static boolean matches(IRNode n) {
        return n instanceof IRCJump;
    }

    /**
     * Traverses an IR tree and returns the list of untraversed 
     * children that are not covered by this tile. The order of the
     * children is the same as the order of tiles in children.
     */
    public static List<IRNode> traverse(IRNode n) {
        IRCJump cj = (IRCJump) n;
        return Arrays.asList(cj.cond);
    }

    @Override
    public <T> T accept(TileVisitor<T> v) {
        return v.visit(this);
    }
}