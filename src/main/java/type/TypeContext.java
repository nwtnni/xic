package type;

public class TypeContext extends Context<Type, Type> {

    public TypeContext() {
        super();
        for (Type t : Type.TYPES) {
            try {
                add(t, Type.UNIT);
            } catch (TypeException e) {
                // impossible when constructing new clean TypeContext
            } 
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
