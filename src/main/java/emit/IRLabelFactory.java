package emit;

import ir.IRLabel;

/**
 * Unique label generators for IR productions.
 */
public class IRLabelFactory {

	private static int labelIndex = 0;
	
	public static IRLabel generate(String name) {
        return new IRLabel(name + "__label_" + Long.toString(++labelIndex));
	}
}
