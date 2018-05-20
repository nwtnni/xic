package xic.phase;

import java.util.List;
import java.util.ArrayList;

import util.Result;

/**
 * The superclass of all compilation phases in the pipeline.
 */
public abstract class Phase {

    public static List<Phase> complete() {
        return new ArrayList<>(List.of(
            new Lex(),
            new Parse(),
            new TypeCheck(),
            new Emit(),
            // new Fold(),
            new Canonize(),
            // new Interpret(),
            // new ConstPropNoPrint(),
            // new FoldNoPrint(),
            // new ConstProp(),
            // new FoldNoPrint(),
            // new CSE(),
            new Irgen(),
            new Tile(),
            // new RegAlloc(),
            new TrivialAlloc()
        ));
    }

    public enum Kind {
        LEX,
        PARSE,
        TYPE,
        EMIT,
        FOLD,
        CANONIZE,
        INTERPRET,
        CONSTPROP,
        CSE,
        IRGEN,
        TILE,
        REG_ALLOC,
        MC,
        ALLOCATE;
    }
    
    // Public for debug purposes
    protected Kind kind;

    protected boolean output;

    public Phase() { output = false; }

    public void setOutput() { output = true; }

    public void setOutputCFG() {}

    public boolean matches(Kind kind) { return this.kind == kind;}

    public abstract Result<Product> process(Config config, Result<Product> previous);
}
