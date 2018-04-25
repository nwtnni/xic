package xic.phase;

import emit.ABIContext;
import emit.ConstantFolder;
import ir.IRCompUnit;

import util.Pair;
import util.Result;

public class Fold extends Phase {

    public Fold() { kind = Phase.Kind.FOLD; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        Pair<IRCompUnit, ABIContext> ir = previous.ok().getEmitted();

        ConstantFolder.constantFold(ir.first); 

        return new Result<>(Product.emitted(ir));
    }
}
