package type;

import org.pcollections.*;

public class TypeContext extends Context<Type, Type> {

    public TypeContext() {
        super();
        for (Type t : Type.TYPES) {
            add(t, Type.UNIT);
        }
    }

    public void validate(Type t, Type p) throws Exception {
        Type ancestor = p;
        while (!ancestor.equals(Type.UNIT)) {
            if (ancestor.equals(t)) {
                throw new Exception("Cyclic type dependency.");
            }
            ancestor = lookup(ancestor);
        }
    }
}
