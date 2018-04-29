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

    /**
     * Returns true if this instruction is a Mov.
     *
     * Used for move coalescing during register allocation.
     */
    public boolean isMove() {
        return false;
    }

    public <T> T accept(InstrVisitor<T> v) {
        return null;
    }
}
