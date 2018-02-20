package type;

import java.util.HashMap;

// Linked list of symbol tables,
// which map String IDs to types.
//
public class Context {

    private Context parent;
    private HashMap<String, Type> table;

    public Context(Context parent) {
        this.parent = parent; 
        this.table = new HashMap<>();
    }

    public Type lookup(String id) {
        Type result = table.get(id);
        return result == null ? parent.lookup(id) : result;
    }

    public void insert(String id, Type t) {
        table.put(id, t);
    }
}
