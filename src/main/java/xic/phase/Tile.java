package xic.phase;

import util.Result;

public class Tile extends Phase {

    public Tile() { kind = Phase.Kind.TILE; }

    @Override
    public Result<Intermediate> process(Config config, Result<Intermediate> previous) {
        // TODO
        return null;
    }
}
