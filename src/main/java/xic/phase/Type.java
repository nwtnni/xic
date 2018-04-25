package xic.phase;

import util.Result;

public class Type extends Phase {

    public Type() { kind = Phase.Kind.TYPE; }

    @Override
    public Result<Intermediate> process(Config config, Result<Intermediate> previous) {
        // TODO
        return null;
    }

}
