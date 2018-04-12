package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import ir.*;
import assemble.*;

public class Call extends Instr {

    public String name;
    public List<Instr> args;

    public Call(String name, List<Instr> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public List<String> toAssembly() {
        return null;
    }
}