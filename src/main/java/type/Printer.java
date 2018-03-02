package type;

import java.io.*;
import xic.FilenameUtils;

import ast.Node;
import parser.XiParser;
import xic.XicException;

public class Printer {
	
	public static void print(String source, String sink, String lib, String unit) throws XicException {
		String ext = FilenameUtils.getExtension(unit);
    	String output = FilenameUtils.concat(sink, FilenameUtils.removeExtension(unit));
        mkDirTo(output);

    	Node ast = null;
        FileWriter writer = null;

        try {
        	try {
            	switch (ext) {
	        		case "xi":
	        			output += ".typed";
	        			ast = XiParser.from(source, unit);
	        			if (lib.equals("")) {
							lib = FilenameUtils.concat(source, FilenameUtils.getFullPath(unit));
						}
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

    private static void mkDirTo(String file) {
        try {
            (new File(file)).getParentFile().mkdirs();
        } catch (NullPointerException e) { }
    }
}