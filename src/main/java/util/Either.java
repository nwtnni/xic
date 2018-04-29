package util;

public class Either<L, R> {

    private enum Kind { LEFT, RIGHT }

    private Kind kind;
    private L left;
    private R right;

    public Either(L left, R right) {
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
        assert kind == Kind.LEFT;
        return left;
    }

    public R getRight() {
        assert kind == Kind.RIGHT;
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Either<?, ?>)) return false;

        Either<?, ?> e = (Either<?, ?>) o;
        if (isLeft() && e.isLeft()) {
            return getLeft().equals(e.getLeft());
        } else if (isRight() && e.isRight()) {
            return getRight().equals(e.getRight());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // GUARANTEED TO BE PRIME
        int prime = (isLeft()) ? 1282519 : 9309831; 
        return (isLeft()) ? getLeft().hashCode() ^ prime : getRight().hashCode() ^ prime; 
    }

    @Override
    public String toString() {
        return String.format("%s of %s, %s", kind, left, right);
    }
}
