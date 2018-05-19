package assemble;

import java.util.List;
import java.util.ArrayList;

public class CompUnit<A> {

    public String name;
    
    public List<FuncDecl<A>> fns;

    public CompUnit() {
        this.name = "COMPUNIT";
        this.fns = new ArrayList<>();
    }

    public CompUnit(String name) {
        this.name = name;
        this.fns = new ArrayList<>();
    }

    public CompUnit(List<FuncDecl<A>> fns) {
        this.fns = fns;
    }

    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(".file \"" + name + "\"");
        instrs.add(".text");
        for (FuncDecl<A> f : fns) {
            instrs.addAll(f.toAssembly());
        }
        return instrs;
    }
}
