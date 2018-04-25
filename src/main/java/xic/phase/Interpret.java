package xic.phase;

import interpret.IRSimulator;
import interpret.IRSimulator.Trap;

import ir.IRCompUnit;
import emit.ABIContext;

import util.Pair;
import util.Result;

public class Interpret extends Phase {

    public Interpret() {
        kind = Phase.Kind.INTERPRET;
        output = true;
    }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        Pair<IRCompUnit, ABIContext> emitted = previous.ok().getEmitted();

        System.out.println("\n--- Intepreting " + config.source + "... ---\n");

        try {
            IRSimulator sim = new IRSimulator(emitted.first);
            sim.call("_Imain_paai", 0);
        } catch (Trap e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n");

        return previous;
    }
}
