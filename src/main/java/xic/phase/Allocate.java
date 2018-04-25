package xic.phase;

import util.Result;

public class Allocate extends Phase {

    public Allocate() { kind = Phase.Kind.ALLOCATE; }

    @Override
    public Result<Intermediate> process(Config config, Result<Intermediate> previous) {
        // TODO
        return null;
    }
}
