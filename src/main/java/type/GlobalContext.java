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

    //TODO: make sure overrides line up
    public void extend(ClassType subclass, ClassType superclass) throws TypeException {


    }

    public boolean contains(ClassType ct) {
        return classes.containsKey(ct.getID());
    }

    public boolean contains(String id) {
        return context.contains(id);
    }

    public GlobalType lookup(String id) {
        return context.lookup(id);
    }

    public ClassContext lookup(ClassType ct) {
        return classes.get(ct.getID());
    }

    public boolean isSubclass(ClassType subclass, ClassType superclass) {

        if (subclass.equals(superclass)) return true;

        ClassType ct = subclass;
        while (hierarchy.containsKey(subclass) && !hierarchy.get(ct).equals(superclass)) {
            ct = hierarchy.get(ct);
        }

        return ct.equals(superclass);
    }
}
