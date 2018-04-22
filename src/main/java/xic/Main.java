package xic;

import java.util.ArrayList;

/**
 * Command line interface for Xic.
 * 
 * Possible flags are:
 * 
 */
public class Main {

    /**
     * Main compiler interface. Usage information can be printed with the --help flag.
     */
    public static void main(String[] args) {
        boolean lexFlag = false;
        boolean parseFlag = false;
        boolean typeFlag = false;
        boolean irGenFlag = false;
        boolean irRunFlag = false;
        boolean helpFlag = false;
        String source = "";
        String sink = "";
        String lib = "";
        String asm = "";
        boolean optFlag = true;
        boolean targetFlag = false;
        String targetOS = "linux";

        ArrayList<String> sourceFiles = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--lex")) {
                lexFlag = true;
            } else if (args[i].equals("--parse")) {
                parseFlag = true;
            } else if (args[i].equals("--typecheck")) {
                typeFlag = true;
            } else if (args[i].equals("--irgen")) {
                irGenFlag = true;
            } else if (args[i].equals("--irrun")) {
                irRunFlag = true;
            } else if (args[i].equals("--help")) {
                helpFlag = true;
            } else if (args[i].equals("-sourcepath") && i + 1 < args.length) {
                source = args[++i];
            } else if (args[i].equals("-D") && i + 1 < args.length) {
                sink = args[++i];
            } else if (args[i].equals("-d") && i + 1 < args.length) {
                asm = args[++i];
            } else if (args[i].equals("-libpath") && i + 1 < args.length){
                lib = args[++i];
            } else if (args[i].equals("-O")) {
                optFlag = false;
            } else if (args[i].equals("-target")) {
                targetFlag = true;
                targetOS = args[++i];
            } else {
                sourceFiles.add(args[i]);
            }
        }

        // Help flag given
        if (helpFlag || !(lexFlag || parseFlag || typeFlag || irGenFlag || irRunFlag || targetFlag)) { 
            displayHelp(); 
            return;
        }
        
        if (targetFlag && !targetOS.equals("linux")) {
        	System.out.println("Unsupported target OS. Must be linux.");
        	return;
        }
        
        Xic xic = new Xic(source, sink, asm, lib);
        
        try {
            for (String unit : sourceFiles) {
                if (lexFlag) { xic.printLexed(unit); }
                if (parseFlag) { xic.printParsed(unit); }
                if (typeFlag) { xic.printTyped(unit); }
                if (irGenFlag || irRunFlag) { xic.printIR(unit, irRunFlag, optFlag); }
                xic.printAssembly(unit, optFlag);
            }
        } catch (XicException e) {
            System.out.println(e.toPrint());
        }
    }

    /**
     * Helper function to display usage information.
     */
    private static void displayHelp() {
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Usage: xic <OPTION>* <OPERATION>+ <FILE>+                                            ");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Where <OPTION> is zero or more of:                                                   ");
        System.out.println("  --help                  : Print synopsis of options                                ");
        System.out.println("  -D          <DIRECTORY> : Output diagnostic files to <DIRECTORY>                   ");
        System.out.println("  -d          <DIRECTORY> : Output assembly files to <DIRECTORY>                     ");
        System.out.println("  -libpath    <DIRECTORY> : Search for interface files in <DIRECTORY>                ");
        System.out.println("  -sourcepath <DIRECTORY> : Search for source files in <DIRECTORY>                   ");
        System.out.println("  -target     <OS>        : Specify the OS for which to generate code                ");
        System.out.println("  -O                      : Disable optimizations                                    ");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Where <OPERATION> is one or more of:                                                 ");
        System.out.println("  --lex                   : For each f.(i)xi, generate lex diagnostic file f.lexed   ");
        System.out.println("  --parse                 : For each f.(i)xi, generate parse diagnostic f.(i)parsed  ");
        System.out.println("  --typecheck             : For each f.xi, generate type check diagnostic f.typed    ");
        System.out.println("  --irgen                 : For each f.xi, generate intermediate representation f.ir ");
        System.out.println("  --irrun                 : For each f.xi, generate interpreted intermediate f.ir.nml");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Where <FILE> is one or more source files to operate on                               ");
        System.out.println("-------------------------------------------------------------------------------------");
    }
}
