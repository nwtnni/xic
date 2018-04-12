package assemble.tiles;

import java.util.List;
import java.util.ArrayList;

import ir.*;
import assemble.*;

public class Call extends Tile {

    public String name;
    public List<Tile> args;

    public Call(String name, List<Tile> args) {
        this.name = name;
        this.args = args;
    }

    /**
     * Takes an IR tree and returns true if this tile can
     * cover a portion of the tree beginning at the root.
     */
    public static boolean matches(IRNode n) {
        return n instanceof IRCall;
    }

    /**
     * Traverses an IR tree and returns the list of untraversed 
     * children that are not covered by this tile. The order of the
     * children is the same as the order of tiles in children.
     */
    public static List<IRNode> traverse(IRNode n) {
        IRCall call = (IRCall) n;
        return call.args;
    }

    @Override
    public <T> T accept(TileVisitor<T> v) {
        return v.visit(this);
    }
}