package type;

import util.OrderedMap;
import util.Either;

import static type.TypeException.Kind.*;

public class ClassContext {

    private OrderedMap<String, FieldType> fields;
    private OrderedMap<String, MethodType> methods;

    public ClassContext() {
        this.fields = new OrderedMap<>();
        this.methods = new OrderedMap<>();
    }

    public boolean contains(String id) {
        return fields.containsKey(id) || methods.containsKey(id);
    }

    public boolean containsField(String id) {
        return fields.containsKey(id);
    }

    public boolean containsMethod(String id) {
        return methods.containsKey(id);
    }

    public Type lookup(String id) {
        FieldType field = fields.get(id);
        return field == null ? methods.get(id) : field;
    }

    public FieldType lookupField(String id) {
        return fields.get(id);
    }

    public MethodType lookupMethod(String id) {
        return methods.get(id);
    }

    public void put(String id, FieldType ft) {
        fields.put(id, ft);
    }

    public void put(String id, MethodType mt) {
        methods.put(id, mt);
    }

    public boolean merge(ClassContext module) {

        this.fields = module.fields;

        // Must have exactly the same methods
        if (methods.keyList().size() != module.methods.keyList().size()) return false;

        for (String method : methods.keyList()) {
            if (!module.containsMethod(method)) return false;

            MethodType impl = module.lookupMethod(method);
            MethodType inter = lookupMethod(method);
            if (!impl.equals(inter)) return false;
        }

        return true;
    }
}
