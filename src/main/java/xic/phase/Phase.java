package xic.phase;

import java.util.List;
import java.util.ArrayList;

import util.Result;

public abstract class Phase {

    public static List<Phase> complete() {
        return new ArrayList<>(List.of(
            new Lex(),
            new Parse(),
            new Type(),
            new Emit(),
            new Fold(),
            new Canonize(),
            new Interpret(),
            new ConstProp(),
            new Fold(),
            new ConstProp(),
            new Fold(),
            new CSE(),
            new Irgen(),
            new Tile(),
            new RegAlloc()
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
        CONST,
        CSE,
        IRGEN,
        TILE,
        REG_ALLOC,
        ALLOCATE;
    }
    
    protected Kind kind;

    protected boolean output;

    public Phase() { output = false; }

    public void setOutput() { output = true; }

    public void setOutputCFG() {}

    public boolean matches(Kind kind) { return this.kind == kind;}

    public abstract Result<Product> process(Config config, Result<Product> previous);
}
