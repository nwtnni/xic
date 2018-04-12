package assemble;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assemble.*;
import assemble.instructions.*;
import emit.ABIContext;

public class TrivialAllocator {

    public static CompUnit allocate(CompUnit unit) {
        TrivialAllocator allocator = new TrivialAllocator(unit);
        return allocator.allocate();
    }

    // Running list of assembly instructions
    private CompUnit unit;

    // Current list of instructions
    List<Instr> instrs;

    // Current function visited
    String funcName;

    // 1 if current function has multiple returns
    int isMultiple;

    // > 0 f visting the args of a function call
    int inCall;

    // 1 if current call has multiple returns else 0
    int callIsMultiple;

    // List of instructions for args of current call
    List<Instr> args;

    private TrivialAllocator(CompUnit unit) {
        this.unit = unit;
        
        this.instrs = new ArrayList<>();
        this.funcName = null;
        this.isMultiple = 0;

        this.inCall = 0;
        this.callIsMultiple = 0;
        this.args = new ArrayList<>();
    }

    private CompUnit allocate() {
        for (FuncDecl fn : unit.fns) {
            allocate(fn);
        }
        return unit;
    }

    private void allocate(FuncDecl fn) {

    }

    private Operand allocate(Instr ins) {
        return null;
    }

    private Operand allocate(Instr ins, Temp t) {
        switch (t.kind) {
            case IMM:
                // TODO: add checks based on parent instruction
                return Operand.imm(t.value);
            case TEMP:
                String name = t.name;
               
                String regex = String.format("(%s)(\\d)", Config.ABSTRACT_ARG_PREFIX);
                Matcher arg = Pattern.compile(regex).matcher(name);
                regex = String.format("(%s)(\\d)", Config.ABSTRACT_RET_PREFIX);
                Matcher ret = Pattern.compile(regex).matcher(name); 

                if (arg.find()) {
                    int i = Integer.parseInt(arg.group(1));
                    
                } else if (ret.find()) {

                } else {

                }
        }
        assert false;
        return null;
    }
}