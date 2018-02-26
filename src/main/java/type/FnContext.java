package type;

import java.util.HashMap;
import java.util.Set;

import xic.XicException;

public class FnContext {
	
	private HashMap<String, FnType> map;
	private String current;
	
	public FnContext() {
		this.map = new HashMap<>();
		this.current = null;
	}
	
	public FnType lookup(String id) {
		return map.get(id);
	}
	
	public void add(String id, FnType type) throws XicException {
		FnType existing = lookup(id);
		
		if (existing == null) {
			map.put(id, type);
		} else if (!existing.equals(type)) {
			//TODO: include both locations in error message?
			throw new TypeException(TypeException.Kind.DECLARATION_CONFLICT, type.location);
		}
	}
	
	public void merge(FnContext context) throws XicException {
		for (String id : context.keySet()) {
			add(id, context.lookup(id));
		}
	}
	
	public void enter(String fn) {
		current = fn;
	}
	
	public Type expectedReturn() {
		FnType type = map.get(current);
		return type.returns;
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
}
