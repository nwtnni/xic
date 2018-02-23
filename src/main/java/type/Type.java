package type;

import ast.XiType;

public class Type {

    public static final Type INT = new Type("int");
    public static final Type BOOL = new Type("bool");
    public static final Type UNIT = new Type("_unit");
    public static final Type VOID = new Type("_void");
    public static final Type POLY = new Type("_poly");

    public static final Type[] TYPES = {INT, BOOL, UNIT, VOID, POLY};

    public enum Kind {
        ARRAY, CLASS,
    }

    public Kind kind;
    public String id;
    public Type child;

    private Type(String id) {
        this.kind = Kind.CLASS;
        this.id = id;
        this.child = null;
    }

    public Type(Type child) {
        this.kind = Kind.ARRAY;
        this.id = null;
        this.child = child;
    }

    public Type(XiType xt) {
        if (xt.isClass()) {
            this.kind = Kind.CLASS;
            this.id = xt.id;
            this.child = null;
        } else {
            this.kind = Kind.ARRAY;
            this.id = null;
            this.child = new Type((XiType) xt.child);
        }
    }

    public boolean isClass() {
        return kind == Kind.CLASS;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) { return false; }

        Type t = (Type) o;

        boolean a_class = isClass();
        boolean b_class = t.isClass();

        if (a_class && b_class) {
            return t.id.equals(id);
        } else if (!a_class && !b_class) {
            return t.child.equals(child);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (isClass()) {
            return id.hashCode();
        } else {
            return 10 * child.hashCode();
        }
    }
}
