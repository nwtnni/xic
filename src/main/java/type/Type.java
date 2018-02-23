package type;

import ast.XiType;

public class Type {

    public static final Type INT = new Type("int");
    public static final Type BOOL = new Type("bool");
    public static final Type UNIT = new Type("unit");
    public static final Type VOID = new Type("void");

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
}
