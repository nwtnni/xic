package xic.phase;

import util.Result;

public class Interpret extends Phase {

    public Interpret() { kind = Phase.Kind.INTERPRET; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {
        // TODO
        return null;
    }
}
