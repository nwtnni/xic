package assemble.instructions;

import java.util.UUID;

public abstract class Instr {
    public Instr() {
        this.id = UUID.randomUUID();
    }

    public UUID id;

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