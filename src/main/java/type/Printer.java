package type;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;

import ast.Node;
import parser.XiParser;
import xic.XicException;

public class Printer {
	
	public static void print(String source, String sink, String unit, String loads) throws XicException {
		String ext = FilenameUtils.getExtension(unit);
    	String output = FilenameUtils.concat(sink, FilenameUtils.removeExtension(unit));
    	Node ast = null;
        FileWriter writer = null;

        try {
        	try {
            	switch (ext) {
	        		case "xi":
	        			output += ".typed";
	        			ast = XiParser.from(source, unit);
	        			if (loads.equals("")) {
							loads = FilenameUtils.concat(source, FilenameUtils.getFullPath(unit));
						}
	        			TypeCheck.check(loads, ast);
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
