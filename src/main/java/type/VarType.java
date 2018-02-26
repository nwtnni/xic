package type;

public class VarType extends Type {

	private String var;
	
	public VarType(String var, Type type) {
		this.kind = type.kind;
		this.children = type.children;
		this.id = type.id;
		this.var = var;
	}
	
	@Override
	public String getVariable() {
		return var;
	}
}
