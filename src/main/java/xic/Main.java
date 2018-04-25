package xic;

import java.util.ArrayList;

import xic.phase.*;
import static xic.phase.Phase.Kind;

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
        String source = "";
        String sink = "";
        String lib = "";
        String asm = "";
        boolean helpFlag = false;
        boolean optFlag = true;
        boolean targetFlag = false;
        String targetOS = "linux";

        Xic xic = new Xic();

        ArrayList<String> sourceFiles = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
            case "--lex":
                xic.setOutput(Kind.LEX);
                break;
            case "--parse":
                xic.setOutput(Kind.PARSE);
                break;
            case "--typecheck":
                xic.setOutput(Kind.TYPE);
                break;
            case "--irgen":
                xic.setOutput(Kind.CANONIZE);
                break;
            case "--irrun":
                xic.addPhase(new Interpret());
                break;
            case "--report-opts":
                displayOpts();
                break;
            case "--optir":
                switch (args[++i]){
                case "initial":
                    xic.setOutput(Kind.EMIT);
                    break;
                case "cf":
                    // TODO
                    xic.setOutput(Kind.FOLD);
                    break;
                default:
                    // TODO
                    assert false;
                }
                break;
            case "--optcfg":
                switch (args[++i]) {
                case "initial":
                    xic.setOutputCFG(Kind.EMIT);
                    break;
                case "cf":
                    // TODO
                    xic.setOutputCFG(Kind.FOLD);
                    break;
                default:
                    // TODO
                    assert false;
                }
                break;
            case "--help":
                displayHelp();
                break;
            case "-sourcepath":
                xic.setSource(args[++i]);
                break;
            case "-D":
                xic.setSink(args[++i]);
                break;
            case "-d":
                xic.setAsm(args[++i]);
                break;
            case "-libpath":
                xic.setLib(args[++i]);
                break;
            case "-O":
                optFlag = false;
                break;
            case "-target":
                targetFlag = true;
                targetOS = args[++i];
                break;
            default:
                xic.addUnit(args[i]);
                break;
            }
        }

        if (targetFlag && !targetOS.equals("linux")) {
        	System.out.println("Unsupported target OS. Must be linux.");
        	return;
        }

        xic.run();
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
        System.out.println("Where <PHASE> is exactly one of:                                                     ");
        System.out.println("  initial : Before any optimizations                                                 ");
        System.out.println("  cf      : Constant folding                                                         ");
        System.out.println("  cp      : Constant propagation                                                     ");
        System.out.println("  reg     : Register allocation                                                      ");
        System.out.println("  mc      : Move coalescing                                                          ");
        System.out.println("  cse     : Common subexpression elimination                                         ");
        System.out.println("  final   : After all optimizations                                                  ");
        System.out.println("-------------------------------------------------------------------------------------");
    }

    /**
     * Helper function to display optimization information.
     */
    private static void displayOpts() {
        System.out.println("cf");
        System.out.println("cp");
        System.out.println("reg");
        System.out.println("mc");
        System.out.println("cse");
    }
}
