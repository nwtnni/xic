package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

public class Call extends Instr {

    public String name;
    public List<Instr> args;
    public int numArgs;
    public int numRet;

    public Call(String name, List<Instr> args, int numRet) {
        this.name = name;
        this.args = args;
        this.numArgs = args.size();
        this.numRet = numRet;
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

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}