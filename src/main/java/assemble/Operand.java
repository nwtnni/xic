package assemble;

import util.Either;

public class Operand extends Either<Temp, Mem<Temp>> {
    private Operand(Temp left, Mem<Temp> right) {
        super(left, right);
    }

    public static Operand temp(Temp temp) {
        return new Operand(temp, null);
    }

    public static Operand mem(Mem<Temp> mem) {
        return new Operand(null, mem);
    }

    public boolean isTemp() {
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

    public Temp getTemp() {
        return super.getLeft();
    }

    public Mem<Temp> getMem() {
        return super.getRight();
    }

    @Deprecated
    public Temp getLeft() {
        assert false;
        return null;
    }

    @Deprecated
    public Mem<Temp> getRight() {
        assert false;
        return null;
    }
}