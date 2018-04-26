package assemble.instructions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import assemble.*;

public abstract class Instr {
    public Instr() {
        this.id = UUID.randomUUID();
        this.live = new HashSet<>();
        this.use = new HashSet<>();
        this.def = new HashSet<>();
    }

    public UUID id;

    // Fields used for live variable analysis
    // Must be initialized with LiveVariableAnnotator before running LV analysis
    public Set<Temp> live;
    public Set<Temp> use;
    public Set<Temp> def;

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Instr) {
            return id.equals(((Instr) o).id);
        }
        return false;
    }

    @Override
    public String toString() {
        return toAbstractAssembly();
    }

    public abstract String toAbstractAssembly();
    
    public abstract String toAssembly();

    public abstract <T> T accept(InsVisitor<T> v);
}