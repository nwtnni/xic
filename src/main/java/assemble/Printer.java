package assemble;

import java.io.*;

import assemble.instructions.*;
import ir.*;
import emit.*;
import type.*;
import ast.*;
import parse.*;
import xic.XicException;
import util.Filename;
import util.Pair;


// for tests
import java.util.Map;
import optimize.*;
import optimize.graph.*;

/**
 * Convenience class to write the result of a lexing run to file.
 */
public class Printer {

    /**
     * Generates assembly for the given file, and outputs all commands
     * to the given output file.
     * 
     * @param source Directory to search for the source
     * @param sink Directory to output the result
     * @param unit Path to the target source file, relative to source
     * @param opt Boolean to determine whether to run optimizations
     * @throws XicException if the Printer was unable to write to the given file
     */
    public static void print(String source, String sink, String lib, String unit, boolean opt) throws XicException {
        String output = Filename.concat(sink, Filename.removeExtension(unit));

        IRCompUnit comp = null;
        FileWriter writer = null;

        try {
            Filename.makePathTo(output);
            writer = new FileWriter(output + ".s");
            try {
                Node ast = XiParser.from(source, unit);
                FnContext context = TypeChecker.check(lib, ast);
                Pair<IRCompUnit, ABIContext> ir = Emitter.emitIR((Program) ast, context);

                comp = ir.first;
                ABIContext mangled = ir.second;

                if (opt) {
                    ConstantFolder.constantFold(comp);
                }
                
                comp = (IRCompUnit) Canonizer.canonize(comp);

                CompUnit u = Tiler.tile(comp, mangled);

                // Begin ASA graph test

                ASAEdgeFactory<Void> aef = new ASAEdgeFactory<>();
                ASAGraphFactory<Void> agf = new ASAGraphFactory<>(u, aef);

                Map<String, ASAGraph<Void>> acfgs = agf.getCfgs();

                CompUnit aAfter = new CompUnit();
                for (ASAGraph<Void> c : acfgs.values()) {
                    c.exportCfg(output, "initial");
                    aAfter.fns.add(c.toASA());
                }

                u = aAfter;

                // end test

                // For debug:
                FileWriter debug = new FileWriter(output + ".asa.s");
                for (String i : u.toAbstractAssembly()) {
                    debug.append(i + "\n");
                }
                debug.close();

                u = TrivialAllocator.allocate(u);
                
                for (String i : u.toAssembly()) {
                    writer.append(i + "\n");
                }
                writer.close();
                
            } catch (XicException xic) {
                writer.close();
                throw xic;
            }
        } catch (IOException io) {
            throw XicException.write(output);
        }
    }

}
