package emit;

import ir.IRLabel;

/**
 * Unique label generators for IR productions.
 */
public class IRLabelFactory {

    private static int labelIndex = 0;

    public static IRLabel generate(String name) {
        String label = String.format("L%04d_%s", labelIndex++, name);
        return new IRLabel(label);
    }
}
