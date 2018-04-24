package util;

import xic.XicException;

public class Result<T> {

    public enum Kind { OK, ERR }

    public Kind kind;
    private T ok;
    private XicException err;

    public Result(T ok) {
        this.kind = Kind.OK;
        this.ok = ok;
        this.err = null;
    }

    public Result(XicException err) {
        this.kind = Kind.ERR;
        this.ok = null;
        this.err = err;
    }

    public T ok() {
        assert kind == Kind.OK;
        return ok;
    }

    public XicException err() {
        assert kind == Kind.ERR;
        return err;
    }
}
