package util;

/** An either type. */
public class Either<L, R> {

    public static interface Matcher<S, T> {
        public T match(S side);
    }

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

    public <T> T match(Matcher<L, T> l, Matcher<R, T> r) {
        switch (kind) {
        case LEFT:
            return l.match(left);
        case RIGHT:
            return r.match(right);
        }
        assert false;
        return null;
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
        // ok actually prime now
        int prime = (isLeft()) ? 1282529 : 9309847; 
        return (isLeft()) ? getLeft().hashCode() ^ prime : getRight().hashCode() ^ prime; 
    }

    @Override
    public String toString() {
        return String.format("%s of %s, %s", kind, left, right);
    }
}
