package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import assemble.*;

public class Jcc extends Instr {

    public enum Kind {
        Z ("z");

        String cond;

        private Kind(String cond) { this.cond = cond; }
    }

    public Kind kind;
    public String target;

    public Jcc(Kind kind, String target) {
        this.kind = kind;
        this.target = target;
    }


    @Override
    public List<String> toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(String.format("j%s %s", kind.cond, target));
    }
}