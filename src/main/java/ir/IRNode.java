package ir;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * A node in an intermediate-representation abstract syntax tree.
 */
public abstract class IRNode {

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     return v.unit();
    // }

    // @Override
    // public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
    //     return v;
    // }

    // @Override
    // public IRNode buildInsnMaps(InsnMapsBuilder v) {
    //     v.addInsn(this);
    //     return this;
    // }

    // @Override
    // public CheckCanonicalIRVisitor checkCanonicalEnter(
    //         CheckCanonicalIRVisitor v) {
    //     return v;
    // }

    // @Override
    // public boolean isCanonical(CheckCanonicalIRVisitor v) {
    //     return true;
    // }

    // @Override
    // public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
    //     return true;
    // }

    public abstract <T> T accept(IRVisitor<T> v);

    public abstract String label();

    public abstract void printSExp(SExpPrinter p);

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw);
             SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
            printSExp(sp);
        }
        return sw.toString();
    }
}
