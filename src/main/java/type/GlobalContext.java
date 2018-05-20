package type;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

import util.Context;
import util.OrderedMap;
import xic.XicInternalException;

import static type.TypeException.Kind;

/**
 * Represents the top-level type context.
 */
public class GlobalContext {

    private Context<String, GlobalType> context;
    private Map<ClassType, ClassType> hierarchy;
    private Map<ClassType, ClassContext> classes;

    public GlobalContext() {
        this.context = new Context<>();
        this.hierarchy = new HashMap<>();
        this.classes = new HashMap<>();
    }

    public void put(String id, ClassContext cc) throws TypeException {
        if (context.contains(id)) throw new TypeException(Kind.DECLARATION_CONFLICT);
        ClassType ct = new ClassType(id);
        context.add(id, ct);
        classes.put(ct, cc);
    }

    public void put(String id, GlobalType gt) {
        if (gt.isClass()) throw new XicInternalException("Attempting to insert class without methods");
        context.add(id, gt);
    }

    public boolean extend(ClassType subclass, ClassType superclass) {

        ClassType ct = superclass;
        while (ct != null) {
            if (ct.equals(subclass)) return false;
            ct = hierarchy.get(ct);
        }

        hierarchy.put(subclass, superclass);
        return true;
    }

    public boolean validate(ClassType subclass) {

        // Early return: no superclass
        if (!hierarchy.containsKey(subclass)) return true;

        ClassContext cc = classes.get(subclass);
        ClassType ct = hierarchy.get(subclass);

        // Make sure all overrides match up
        while (ct != null) {

            // Early return: superclass doesn't exist
            if (!classes.containsKey(ct)) return false;

            ClassContext parent = classes.get(ct);

            // Compare methods to parent
            for (String method : cc.getMethods()) {

                if (!parent.containsMethod(method)) continue;

                // Make sure method types are equal
                if (!parent.lookupMethod(method).equals(cc.lookupMethod(method))) return false;
            }

            // Traverse upward through hierarchy
            ct = hierarchy.get(ct);
        }

        return true;
    }

    public boolean contains(ClassType ct) {
        return classes.containsKey(ct);
    }

    public boolean contains(String id) {
        return context.contains(id);
    }

    public GlobalType lookup(String id) {
        return context.lookup(id);
    }

    public ClassContext lookup(ClassType ct) {
        return classes.get(ct);
    }

    public OrderedMap<String, MethodType> lookupAllMethods(ClassType ct) {

        // Record stack of ancestors
        Stack<ClassType> ancestors = new Stack<>();
        OrderedMap<String, MethodType> methods = new OrderedMap<>();

        // Start off at subclass
        ClassType traverse = ct;
        ancestors.push(traverse);

        // Traverse upwards through hierarchy
        while (hierarchy.containsKey(traverse)) {
            traverse = hierarchy.get(traverse);
            ancestors.push(traverse);
        }

        // Add methods top-down to preserve correct order
        while (!ancestors.isEmpty()) {

            ClassContext ancestor = classes.get(ancestors.pop());

            for (String method : ancestor.getMethods()) {

                // Method already defined in superclass
                if (methods.containsKey(method)) continue;

                // Otherwise add it to the ordered map
                methods.put(method, ancestor.lookupMethod(method));
            }
        }

        return methods;
    }

    public boolean isSubclass(ClassType subclass, ClassType superclass) {

        ClassType ct = subclass;
        while (hierarchy.containsKey(ct) && !ct.equals(superclass)) {
            ct = hierarchy.get(ct);
        }

        return ct.equals(superclass);
    }

    public void merge(GlobalContext other) {

    }
}
