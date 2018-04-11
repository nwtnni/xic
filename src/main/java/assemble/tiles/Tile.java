package assemble.tiles;

import ir.*;
import assemble.*;

public abstract class Tile {

    public String[] instructions;
    public String tempDest;
    public String[] tempSrc;

    public Operand dest;
    public Operand[] src;

    public Tile(String[] instructions) {
        this.instructions = instructions;
    }

    /**
     * Takes an IR tree and returns true if this tile can
     * cover a portion of the tree beginning at the root.
     */
    public abstract boolean match(IRNode n);

    public abstract <T> T accept(TileVisitor<T> v);
}