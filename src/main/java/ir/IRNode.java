package ir;

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

    @Override
    public String toString() {
        return Printer.toString(this);
    }
}
