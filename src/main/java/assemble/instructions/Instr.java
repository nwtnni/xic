package assemble.instructions;

import java.util.List;
import java.util.UUID;

public abstract class Instr {
    public Instr() {
        this.id = UUID.randomUUID();
    }

    public UUID id;

    public abstract List<String> toAbstractAssembly();
    
    public abstract List<String> toAssembly();

    public abstract <T> T accept(InsVisitor<T> v);
}