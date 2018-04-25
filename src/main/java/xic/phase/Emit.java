package xic.phase;

import util.Result;

public class Emit extends Phase {

    public Emit() { kind = Phase.Kind.EMIT; }

    @Override
    public Result<Intermediate> process(Result<Intermediate> previous) {
        // TODO
        return null;
    }
}
