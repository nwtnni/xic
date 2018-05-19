package assemble;

import java.util.List;
import java.util.ArrayList;

import assemble.instructions.*;

public class CompUnit<A> {

    public String name;

    public List<String> data;
    
    public List<FuncDecl<A>> text;

    public CompUnit() {
        this.name = "COMPUNIT";
        this.text = new ArrayList<>();
    }

    public CompUnit(String name) {
        this.name = name;
        this.data = new ArrayList<>();
        this.text = new ArrayList<>();
    }

    public CompUnit(List<FuncDecl<A>> fns) {
        this.text = fns;
    }

    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        instrs.add(".file \"" + name + "\"");
        
        instrs.add(".data");
        for (String directive : data) {
            instrs.add(directive);
        }
        
        instrs.add(".section .ctors");
        instrs.add(".align 4");
        instrs.add(".quad   _I_init_p");

        instrs.add(".text");
        for (FuncDecl<A> f : text) {
            instrs.addAll(f.toAssembly());
        }
        return instrs;
    }
}
