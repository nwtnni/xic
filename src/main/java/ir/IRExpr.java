package ir;

public interface IRExpr extends IRNode {
    boolean isConstant();

    long constant();
}
