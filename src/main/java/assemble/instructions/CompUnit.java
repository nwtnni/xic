package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

public class CompUnit {
    public List<FuncDecl> fns;

    public CompUnit() {
        this.fns = new ArrayList<>();
    }

    public CompUnit(List<FuncDecl> fns) {
        this.fns = fns;
    }

    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(".text");
        for (FuncDecl f : fns) {
            instrs.addAll(f.toAssembly());
        }
        return instrs;
    }

}