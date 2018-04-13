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
        output = Filename.setExtension(output, "s");

        IRCompUnit comp = null;
        FileWriter writer = null;

        try {
            Filename.makePathTo(output);
            writer = new FileWriter(output);
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
                String cmds = Assembler.assemble(comp, mangled);
                // Generate .s file
                writer.write(cmds);
                writer.close();

                CompUnit u = Tiler.tile(comp, mangled);
                u = TrivialAllocator.allocate(u);
                for (String i : u.toAssembly()) {
                    System.out.println(i); 
                }
                
            } catch (XicException xic) {
                writer.close();
                throw xic;
            }
        } catch (IOException io) {
            throw XicException.write(output);
        }
    }

}
