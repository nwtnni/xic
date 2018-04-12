package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import ir.*;
import assemble.*;

public class Jcc extends Instr {

    public enum Kind {
        Z ("z");

        String cond;

        private Kind(String cond) { this.cond = cond; }
    }

    public Kind kind;
    public String target;
    public String cond;

    public Jcc(Kind kind, String target, String cond) {
        this.kind = kind;
        this.target = target;
        this.cond = cond;
    }

    @Override
    public List<String> toAssembly() {
        return null;
    }
}