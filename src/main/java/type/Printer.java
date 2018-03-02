package type;

import java.io.*;
import xic.FilenameUtils;

import ast.Node;
import parser.XiParser;
import xic.XicException;

/**
 * Convenience class to write the result of a type check run to file.
 */
public class Printer {
	
	/**
	 * Parses the given file, runs the type checker, and outputs diagnostic
	 * information to the given output file as well as System.out.
	 * 
	 * @param source Directory to search for the source
	 * @param sink Directory to output the result
	 * @param lib Directory to search for interface files
 	 * @param unit Path to the target source file, relative to source
	 * @throws XicException if the Printer was unable to write to the given file
	 */
	public static void print(String source, String sink, String lib, String unit) throws XicException {
		String ext = FilenameUtils.getExtension(unit);
    	String output = FilenameUtils.concat(sink, FilenameUtils.removeExtension(unit));
	    output = FilenameUtils.setExtension(output, "typed");
        FilenameUtils.makePathTo(output);

    	Node ast = null;
        FileWriter writer = null;

        try {
        	try {
            	switch (ext) {
	        		case "xi":
	        			ast = XiParser.from(source, unit);
	        			TypeCheck.check(lib, ast);
	        			break;
	        		default:
	        			throw XicException.unsupported(unit);
            	}
    	
	            writer = new FileWriter(output);
	            writer.write("Valid Xi Program");
	        	writer.close();
	            
	    	} catch (XicException xic) {
	            writer = new FileWriter(output);
	            writer.write(xic.toWrite());
	        	writer.close();
	            throw xic;
	    	}
        } catch (IOException io) {
        	throw XicException.write(output);
        }
	}
}