package assemble.instructions;

import java.util.Set;
import java.util.UUID;

import assemble.*;

public abstract class Instr {
    public Instr() {
        this.id = UUID.randomUUID();
        this.live = null;
    }

    public UUID id;

    public Set<Temp> live;

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