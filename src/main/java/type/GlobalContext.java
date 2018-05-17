package type;

import java.util.Map;
import java.util.HashMap;

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
    private Map<ClassType, OrderedMap<String, Type>> classes;

    public GlobalContext() {
        this.context = new Context<>();
        this.hierarchy = new HashMap<>();
        this.classes = new HashMap<>();
    }

    public void put(String id, OrderedMap<String, Type> contents) throws TypeException {
        if (context.contains(id)) throw new TypeException(Kind.DECLARATION_CONFLICT);
        ClassType ct = new ClassType(id);
        context.add(id, ct);
        classes.put(ct, contents);
    }

    //TODO: make sure overrides line up
    public void extend(ClassType subclass, ClassType superclass) throws TypeException {


    }

    public void put(String id, GlobalType gt) {
        if (gt.isClass()) throw new XicInternalException("Attempting to insert class without methods"); 
        context.add(id, gt);
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
