package assemble;

import util.Either;

public class RegOperand extends Either<Reg, Mem<Reg>> {

    private RegOperand(Reg left, Mem<Reg> right) {
        super(left, right);
    }

    public static RegOperand of(Reg t) {
        return new RegOperand(t, null);
    }

    public static RegOperand of(Mem<Reg> m) {
        return new RegOperand(null, m);
    }

    public boolean isReg() {
        return super.isLeft();
    }

    public boolean isMem() {
        return super.isRight();
    }

    @Deprecated
    public boolean isLeft() {
        assert false;
        return false;
    }

    @Deprecated
    public boolean isRight() {
        assert false;
        return false;
    }

    public Reg getTemp() {
        return super.getLeft();
    }

    public Mem<Reg> getMem() {
        return super.getRight();
    }

    @Deprecated
    public Reg getLeft() {
        assert false;
        return null;
    }

    @Deprecated
    public Mem<Reg> getRight() {
        assert false;
        return null;
    }
}
