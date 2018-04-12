package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import ir.*;
import assemble.*;

public class FuncDecl {
    public List<Instr> prelude;
    public List<Instr> stmts;
    public List<Instr> epilogue;
    
    public FuncDecl(List<Instr> prelude, List<Instr> stmts, List<Instr> epilogue) {
        this.prelude = prelude;
        this.stmts = stmts;
        this.epilogue = epilogue;
    }
    
}