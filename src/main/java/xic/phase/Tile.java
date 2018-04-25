package xic.phase;

import ir.IRCompUnit;
import emit.ABIContext;

import assemble.instructions.CompUnit;
import assemble.Tiler;

import util.Pair;
import util.Result;

public class Tile extends Phase {

    public Tile() { kind = Phase.Kind.TILE; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous; 

        Pair<IRCompUnit, ABIContext> emitted = previous.ok().getEmitted(); 

        CompUnit tiled = Tiler.tile(emitted.first, emitted.second);
        
        return new Result<>(Product.assembled(tiled));
    }
}
