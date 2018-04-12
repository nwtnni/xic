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

    public T visit(CompUnit n) {
        return null;
    }

    public T visit(FuncDecl n) {
        return null;
    }

    public T visit(BinOp n) {
        return null;
    }

    public T visit(Call n) {
        return null;
    }

    public T visit(CJump n) {
        return null;
    }

    public T visit(Jump n) {
        return null;
    }

    public T visit(Const n) {
        return null;
    }

    public T visit(Temp n) {
        return null;
    }

}