package xic.phase;

import util.Result;

public class Allocate extends Phase {

    public Allocate() { kind = Phase.Kind.ALLOCATE; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {
        // TODO
        return null;
    }
}
