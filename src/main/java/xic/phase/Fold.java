package xic.phase;

import util.Result;

public class Fold extends Phase {

    public Fold() { kind = Phase.Kind.FOLD; }

    @Override
    public Result<Intermediate> process(Config config, Result<Intermediate> previous) {
        // TODO
        return null;
    }
}
