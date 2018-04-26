package xic;

import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.HashSet;

import xic.phase.Config;
import xic.phase.Phase;
import xic.phase.Product;

import util.Result;

/**
 * Main compiler class. Wraps around and provides convenience methods
 * for every phase of compilation.
 */
public class Xic {

    private String source;

    private String sink;

    private String asm;

    private String lib;

    private List<String> units;

    private List<Phase> phases;

    public Xic() {
        source = ""; 
        sink = "";
        asm = "";
        lib = "";
        units = new ArrayList<>();
        phases = Phase.complete();
    }

    public void setSource(String source) { this.source = source; }

    public void setSink(String sink) { this.sink = sink; }

    public void setAsm(String asm) { this.asm = asm; }

    public void setLib(String lib) { this.lib = lib; }

    public void addUnit(String unit) { this.units.add(unit); }

    public void removePhase(Phase.Kind kind) {
        phases.removeIf(phase -> phase.matches(kind));
    }

    public void setOutput(Phase.Kind kind) {
        for (Phase phase : phases) {
            if (phase.matches(kind)) phase.setOutput();
        }
    }

    public void setOutputCFG(Phase.Kind kind) {
        for (Phase phase : phases) {
            if (phase.matches(kind)) phase.setOutputCFG();
        }
    }

    public void run() {

        Config config = new Config(source, sink, asm, lib);

        for (String unit : units) {

            config.setUnit(unit);
            Result<Product> product = new Result<>(Product.empty());

            for (Phase phase : phases) {
                product = phase.process(config, product);
            }

            if (product.isErr()) {
                System.out.println(product.err().toPrint());
            }
        }
    }
}
