package xic.phase;

import util.Result;

public class Tile extends Phase {

    public Tile() { kind = Phase.Kind.TILE; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {
        // TODO
        return null;
    }
}
