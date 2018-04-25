package xic.phase;

import util.Result;

public class Canonize extends Phase {

    public Canonize() { kind = Phase.Kind.CANONIZE; }

    @Override
    public Result<Intermediate> process(Config config, Result<Intermediate> previous) {
        // TODO
        return null;
    }
}
