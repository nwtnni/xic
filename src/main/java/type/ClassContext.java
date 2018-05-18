package type;

import util.OrderedMap;
import util.Either;

public class ClassContext {

    private OrderedMap<String, FieldType> fields;
    private OrderedMap<String, MethodType> methods;

    public boolean contains(String id) {
        return fields.containsKey(id) || methods.containsKey(id);
    }

    public Either<FieldType, MethodType> lookup(String id) {
        if (fields.containsKey(id)) return Either.left(fields.get(id));
        if (methods.containsKey(id)) return Either.right(methods.get(id));
        return null;
    }
}
