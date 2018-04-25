package xic.phase;

import util.Result;

public class Interpret extends Phase {

    public Interpret() { kind = Phase.Kind.INTERPRET; }

    @Override
    public Result<Intermediate> process(Result<Intermediate> previous) {
        // TODO
        return null;
    }
}
