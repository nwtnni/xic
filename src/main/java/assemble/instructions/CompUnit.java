package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

public class CompUnit {
    protected List<FuncDecl> fns;

    public CompUnit(List<FuncDecl> fns) {
        this.fns = fns;
    }

    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        for (FuncDecl f : fns) {
            instrs.addAll(f.toAssembly());
        }
        return instrs;
    }

}