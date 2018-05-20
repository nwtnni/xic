package type;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

import util.Context;
import util.OrderedMap;
import util.Pair;
import xic.XicInternalException;

import static type.TypeException.Kind;

/**
 * Represents the top-level type context.
 */
public class GlobalContext {

    private Map<String, GlobalType> context;
    private Map<ClassType, ClassType> hierarchy;
    private Map<ClassType, ClassContext> classes;
    private Set<String> local;

    public GlobalContext() {
        this.context = new HashMap<>();
        this.hierarchy = new HashMap<>();
        this.classes = new HashMap<>();
        this.local = new HashSet<>();
    }

    public void put(String id, ClassContext cc) {
        ClassType ct = new ClassType(id);
        context.put(id, ct);
        classes.put(ct, cc);
    }

    public void put(String id, GlobalType gt) {
        if (gt.isClass()) throw new XicInternalException("Attempting to insert class without methods");
        context.put(id, gt);
    }

    public void setLocal(String id) {
        local.add(id);
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

            // Compare fields to parent
            for (String field : cc.getFields()) {
                if (parent.contains(field)) return false;
            }

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

    public boolean isLocal(String id) {
        return local.contains(id);
    }

    public boolean contains(ClassType ct) {
        return classes.containsKey(ct);
    }

    public boolean contains(String id) {
        return context.containsKey(id);
    }

    public Type inherits(ClassType ct, String id) {

        // Seearch through class and all ancestors
        do {
            ClassContext cc = classes.get(ct);
            if (cc != null) {
                if (cc.contains(id)) return cc.lookup(id);
            }
            ct = hierarchy.get(ct);
        } while (ct != null);

        return null;
    }

    public GlobalType lookup(String id) {
        return context.get(id);
    }

    public ClassContext lookup(ClassType ct) {
        return classes.get(ct);
    }

    public Pair<ClassType, List<String>> lookupFieldSource(ClassType sub, String name) {
        while (true) {
            if (sub == null || !classes.containsKey(sub)) throw new XicInternalException("Field not bound in any superclass");

            // Keep traversing hierarchy
            if (!classes.get(sub).containsField(name)) {
                sub = hierarchy.get(sub);
                continue;
            }

            // Otherwise get context
            ClassContext cc = classes.get(sub);
            return new Pair<>(sub, cc.getFields());

        }
    }

    public Set<ClassType> lookupLocalClasses() {
        return local.stream()
            .map(id -> new ClassType(id))
            .filter(id -> classes.containsKey(id))
            .collect(Collectors.toSet());
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

        // Dummy method name
        int n = 0;

        // Add methods top-down to preserve correct order
        while (!ancestors.isEmpty()) {

            ClassType ancestorClass = ancestors.pop();
            ClassContext ancestor = classes.get(ancestorClass);

            // Spacer "method" for IR generation
            methods.put(Integer.toString(n), new MethodType(ancestorClass, new ArrayList<>(), new ArrayList<>()));
            n += 1;

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

    /**
     * Merges the interface GlobalContext [other] into this GlobalContext.
     */
    public boolean merge(GlobalContext other) {

        // Check all top-level declarations for conflicts
        for (String name : other.context.keySet()) {

            GlobalType reference = other.context.get(name);

            // No conflict possible
            if (!context.containsKey(name)) {
                context.put(name, reference);
                continue;
            }

            GlobalType current = context.get(name);

            // Functions are allowed to shadow if their types are the same
            if (current.isFn() && reference.isFn() && current.equals(reference)) continue;

            // Otherwise must be a namespace conflict (no globals in interfaces)
            return false;
        }

        // Update classes
        for (ClassType ct : other.classes.keySet()) {

            // All classes must be unique
            if (classes.containsKey(ct)) throw new XicInternalException("Inconsistent GlobalContext state");

            // Otherwise update current context
            classes.put(ct, other.classes.get(ct));
        }

        // Update hierarcy
        for (ClassType ct : other.hierarchy.keySet()) {
            hierarchy.put(ct, other.hierarchy.get(ct));
        }

        return true;
    }
}
