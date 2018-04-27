package util;

public class Either<L, R> {

    private enum Kind { LEFT, RIGHT }

    private Kind kind;
    private L left;
    private R right;

    private Either(L left, R right) {
        this.kind = (left == null) ? Kind.RIGHT : Kind.LEFT;
        this.left = left; 
        this.right = right;
    }
    
    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(left, null);
    }

    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(null, right);
    }

    public boolean isLeft() {
        return kind == Kind.LEFT;
    }

    public boolean isRight() {
        return kind == Kind.RIGHT;
    }

    public L getLeft() {
        assert isLeft();
        return left;
    }

    public R getRight() {
        assert isRight();
        return right;
    }
}
