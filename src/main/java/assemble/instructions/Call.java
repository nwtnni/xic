package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import assemble.*;

public class Call extends Instr {

    public String name;
    public List<Instr> args;

    public List<Operand> returns;

    public Call(String name, List<Instr> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public List<String> toAbstractAssembly() {
        List<String> instrs = new ArrayList<>();
        for (Instr i : args) {
            instrs.addAll(i.toAbstractAssembly());
        }
        instrs.add("callq " + name);
        return instrs;
    }

    @Override
    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        for (Instr i : args) {
            instrs.addAll(i.toAssembly());
        }
        instrs.add("callq " + name);
        return instrs;
    }
}