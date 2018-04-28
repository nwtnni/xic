package optimize.propagate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

import util.Pair;

import ir.*;

/** Utility class to generate a set of temp to constant mappings at each node. */
public class ConstInitVisitor extends IRVisitor<Void> {

    /** Returns the mapping of statements to temps that are defined as constants. */
    public static Map<IRStmt, Pair<IRTemp, Optional<IRConst>>> getConstDefs(Set<IRStmt> nodes) {

        ConstInitVisitor visitor = new ConstInitVisitor();
        for (IRStmt node : nodes) {
            node.accept(visitor);
        }

        return visitor.mapping;
    }

    /** 
     * Mapping of statements that generate constants to the pair of the temp and the constant. 
     * If the optional is empty, then the variable maps to NAC.
     * */
    Map<IRStmt, Pair<IRTemp, Optional<IRConst>>> mapping;

    /** Initialize the mapping. */
    private ConstInitVisitor() {
        this.mapping = new HashMap<>();
    }

    /*
     * Statement nodes
     */
    
    public Void visit(IRMove m) {
        Optional<IRConst> c = Optional.empty();
        if (m.target() instanceof IRTemp && m.src() instanceof IRConst) {
            c = Optional.of((IRConst) m.src());
        }
        mapping.put(m, new Pair<>((IRTemp) m.target(), c));
        return null;
    }
    
}