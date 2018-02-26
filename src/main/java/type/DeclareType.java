package type;

public class DeclareType extends Type {

	private String var;
	
	public DeclareType(String var, Type type) {
		this.kind = type.kind;
		this.children = type.children;
		this.id = type.id;
		this.var = var;
	}
	
	@Override
	public String getDeclaration() {
		return var;
	}
	
	@Override 
	public boolean isDeclaration() {
		return true;
	}
}
