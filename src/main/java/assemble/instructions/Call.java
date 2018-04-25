package assemble.instructions;

public class Call extends Instr {

    public String name;
    public int numArgs;
    public int numRet;

    public Call(String name, int numArgs, int numRet) {
        this.name = name;
        this.numArgs = numArgs;
        this.numRet = numRet;
    }

    @Override
    public String toAbstractAssembly() {
        return "callq " + name;
    }

    @Override
    public String toAssembly() {
        return "callq " + name;
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}