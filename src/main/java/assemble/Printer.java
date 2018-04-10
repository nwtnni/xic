package assemble;

import java.io.*;
import xic.FilenameUtils;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import parser.XiSymbol;
import xic.XicException;

import ir.*;
import emit.*;
import type.*;
import parser.*;
import lexer.*;
import ast.*;
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
        String output = FilenameUtils.concat(sink, FilenameUtils.removeExtension(unit));
        output = FilenameUtils.setExtension(output, "S");

        IRCompUnit comp = null;
        FileWriter writer = null;

        try {
            FilenameUtils.makePathTo(output);
            writer = new FileWriter(output);
            try {
                Node ast = XiParser.from(source, unit);
                FnContext context = TypeChecker.check(lib, ast);
                comp = Emitter.emitIR((Program) ast, context);

                if (opt) {
                    ConstantFolder.constantFold(comp);
                }
                
                comp = (IRCompUnit) Canonizer.canonize(comp);
                String cmds = Assembler.assemble(comp, new ABIContext(context));
                // Generate .s file
                writer.write(cmds);
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
