package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import ir.*;
import assemble.*;

public class CompUnit {
    protected List<FuncDecl> fns;

    public CompUnit(List<FuncDecl> fns) {
        this.fns = fns;
    }

}