package assemble.instructions;

import java.util.UUID;

import assemble.*;

public abstract class Instr<A> {

    public Instr() {
        this.id = UUID.randomUUID();
    }

    public UUID id;

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

    public <T> T accept(InsVisitor<T> v) {
        return null;
    }
}
