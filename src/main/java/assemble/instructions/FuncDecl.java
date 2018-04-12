package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

public class FuncDecl {
    public List<Instr> prelude;
    public List<Instr> stmts;
    public List<Instr> epilogue;
    
    public FuncDecl(List<Instr> prelude, List<Instr> stmts, List<Instr> epilogue) {
        this.prelude = prelude;
        this.stmts = stmts;
        this.epilogue = epilogue;
    }
    
    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        for (Instr i : prelude) {
            instrs.addAll(i.toAssembly());
        }
        for (Instr i : stmts) {
            instrs.addAll(i.toAssembly());
        }
        for (Instr i : epilogue) {
            instrs.addAll(i.toAssembly());
        }
        return instrs;
    }
}