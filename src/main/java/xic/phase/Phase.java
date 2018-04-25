package xic.phase;

import util.Result;

abstract class Phase implements Comparable<Phase> {

    public enum Kind {
        LEX,
        PARSE,
        TYPE,
        EMIT,
        FOLD,
        CANONIZE,
        INTERPRET
    }
    
    protected Kind kind;
    protected boolean output;

    public Phase() { output = false; }

    public void setOutput() { output = true; }

    public abstract Result<Intermediate> process(Result<Intermediate> previous);

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
