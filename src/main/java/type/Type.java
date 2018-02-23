package type;

import ast.XiType;

public class Type {

    public enum Kind {
        ARRAY, CLASS,
    }

    public Kind kind;
    public String id;
    public Type child;
    
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
