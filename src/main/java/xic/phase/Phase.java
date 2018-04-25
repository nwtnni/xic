package xic.phase;

import java.util.List;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import util.Result;

public abstract class Phase implements Comparable<Phase> {

    public static SortedSet<Phase> minimal() {
        return new TreeSet<>(
            List.of(
                new Lex(),
                new Parse(),
                new Type(),
                new Emit(),
                new Canonize(),
                new Tile(),
                new Allocate()
            )
        );
    }

    public enum Kind {
        LEX,
        PARSE,
        TYPE,
        EMIT,
        FOLD,
        CANONIZE,
        INTERPRET,
        TILE,
        ALLOCATE,
    }
    
    protected Kind kind;
    protected boolean output;

    public Phase() { output = false; }

    public void setOutput() { output = true; }

    public boolean matches(Kind kind) { return this.kind == kind; }

    public abstract Result<Product> process(Config config, Result<Product> previous);

    @Override
    public int compareTo(Phase p) {
        return kind.compareTo(p.kind); 
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Phase)) return false;
        Phase p = (Phase) o;
        return kind.equals(p.kind);
    }

    @Override
    public int hashCode() {
        return kind.hashCode();
    }
}
