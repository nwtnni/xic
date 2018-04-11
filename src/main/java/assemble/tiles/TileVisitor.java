package assemble.tiles;

import java.util.List;
import java.util.ArrayList;

public abstract class TileVisitor<T> {

    /*
     * Psuedo-visit method for visiting a list of nodes.
     */
    public List<T> visit(List<Tile> nodes) {
        List<T> t = new ArrayList<>();
        for (Tile n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }

}