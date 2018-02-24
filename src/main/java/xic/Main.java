package xic;

import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

public class Main {

	public static void main(String[] args) {
		boolean lexFlag = false;
		boolean parseFlag = false;
		boolean helpFlag = false;
		String sourcePath = "";
		String dPath = "";

		ArrayList<String> sourceFiles = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--lex")) {
				lexFlag = true;
			} else if (args[i].equals("--parse")) {
				parseFlag = true;
			} else if (args[i].equals("--help")) {
				helpFlag = true;
			} else if (args[i].equals("-sourcepath") && i + 1 < args.length) {
				sourcePath = args[++i];
			} else if (args[i].equals("-D") && i + 1 < args.length) {
				dPath = args[++i];
			} else {
				sourceFiles.add(args[i]);
			}
		}

		// Help flag given
		if (helpFlag || !(lexFlag || parseFlag)) { 
			displayHelp(); 
			return;
		}
		
		// Invalid file given
		for (String unit : sourceFiles) {
			String ext = FilenameUtils.getExtension(unit);
			if (!(ext.equals("ixi") || ext.equals("xi"))) {
				displayHelp();
				return;
			}
		}
		
		Xic xic = new Xic(sourcePath, dPath);
			
		for (String unit : sourceFiles) {
			if (lexFlag) {
				xic.writeLex(unit);
			}
				
			if (parseFlag) {
				if (FilenameUtils.getExtension(unit).equals("ixi")) {
					xic.writeInterface(unit);
				} else {
					xic.writeSource(unit);
				}
			}
		}
	}

	private static void displayHelp() {
		System.out.println("Usage: xic [options] <source-files>");
		System.out.println("  --help:                 Print a synopsis of options");
		System.out.println(
				"  --lex <source-files>:   For each source file filename.xi, generate a lexed file filename.lexed");
		System.out.println(
				"  --parse <source-files>: For each source file filename.xi/filename.ixi, generate a parsed file filename.parsed/filename.iparsed");
	}
}
