package xic;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import static xic.phase.Phase.Kind;

/**
 * Command line interface for Xic.
 *
 * Possible flags are displayed by running xic --help
 *
 */
public class Main {

    enum State { NONE, DISABLE, ENABLE, DISABLE_ALL }

    private static Set<Kind> opts = new HashSet<>();
    private static State state;

    /**
     * Main compiler interface. Usage information can be printed with the --help flag.
     */
    public static void main(String[] args) {
        String targetOS = "linux";
        boolean targetFlag = false;

        opts.add(Kind.INTERPRET);
        state = State.NONE;

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
                opts.remove(Kind.INTERPRET);
                break;
            case "--report-opts":
                displayOpts();
                return;
            case "--optir":
                switch (args[++i]){
                case "initial":
                    xic.setOutput(Kind.EMIT);
                    break;
                case "cf":
                    xic.setOutput(Kind.FOLD);
                    break;
                case "cp":
                    xic.setOutput(Kind.CONSTPROP);
                    break;
                case "cse":
                    xic.setOutput(Kind.CSE);
                    break;
                case "final":
                    xic.setOutput(Kind.IRGEN);
                    break;
                default:
                    // TODO: check this
                    System.out.println("Error: ignoring unknown phase for output.");
                }
                break;
            case "--optcfg":
                switch (args[++i]) {
                case "initial":
                    xic.setOutputCFG(Kind.EMIT);
                    break;
                case "cf":
                    xic.setOutputCFG(Kind.FOLD);
                    break;
                case "cp":
                    xic.setOutputCFG(Kind.CONSTPROP);
                    break;
                case "cse":
                    xic.setOutputCFG(Kind.CSE);
                    break;
                case "final":
                    xic.setOutputCFG(Kind.IRGEN);
                    break;
                default:
                    // TODO: check this
                    System.out.println("Error: ignoring unknown phase for output.");
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
                if (state != State.NONE && state != State.DISABLE_ALL) {
                    System.out.println("Error: ignoring conflicting flag -O.");
                } else {
                    opts.addAll(Set.of(Kind.FOLD, Kind.CONSTPROP, Kind.CSE, Kind.REG_ALLOC, Kind.MC));
                    state = State.DISABLE_ALL;
                }
                break;
            case "-target":
                targetFlag = true;
                targetOS = args[++i];
                break;
            default:
                // Parse options for enabling and disabling optimizations
                if (parseOpt(args[i])) break;

                // Add source file
                xic.addUnit(args[i]);
                break;
            }
        }

        if (args.length == 0) {
            displayHelp();
            return;
        }

        if (targetFlag && !targetOS.equals("linux")) {
        	System.out.println("Error: unsupported target OS. Must be linux.");
        	return;
        }

        for (Kind phase : opts) {
            xic.removePhase(phase);
        }

        xic.run();
    }

    private static Kind optToPhase(String opt) {
        switch (opt) {
            case "-Ocf":
            case "-O-no-cf":
                return Kind.FOLD;
            case "-Ocp":
            case "-O-no-cp":
                return Kind.FOLD;
            case "-Ocse":
            case "-O-no-cse":
                return Kind.CSE;
            case "-Oreg":
            case "-O-no-reg":
                return Kind.REG_ALLOC;
            case "-Omc":
            case "-O-no-mc":
                return Kind.REG_ALLOC;
            default:
                return null;
        }
    }

    private static boolean parseOpt(String opt) {
        switch (opt) {
        case "-Ocf":
        case "-Ocp":
        case "-Ocse":
        case "-Oreg":
        case "-Omc":
            if (state == State.NONE || state == State.ENABLE) {
                opts.remove(optToPhase(opt));
                state = State.ENABLE;
            } else {
                System.out.println("Error: ignoring conflicting flag " + opt + ".");
            }
            return true;
        case "-O-no-cf":
        case "-O-no-cp":
        case "-O-no-cse":
        case "-O-no-reg":
        case "-O-no-mc":
            if (state == State.NONE || state == State.DISABLE) {
                opts.add((optToPhase(opt)));
                state = State.DISABLE;
            } else {
                System.out.println("Error: ignoring conflicting flag " + opt + ".");
            }
            return true;
        default:
            return false;
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
        System.out.println("  --report-opts           : Print synopsis of optimizations available                ");
        System.out.println("  -D          <DIRECTORY> : Output diagnostic files to <DIRECTORY>                   ");
        System.out.println("  -d          <DIRECTORY> : Output assembly files to <DIRECTORY>                     ");
        System.out.println("  -libpath    <DIRECTORY> : Search for interface files in <DIRECTORY>                ");
        System.out.println("  -sourcepath <DIRECTORY> : Search for source files in <DIRECTORY>                   ");
        System.out.println("  -target     <OS>        : Specify the OS for which to generate code                ");
        System.out.println("  --optir     <PHASE>     : Generate .ir file for phase <PHASE>                      ");
        System.out.println("  --optcfg    <PHASE>     : Generate .dot file for phase <PHASE>                     ");
        System.out.println("  -O<opt>                 : Enable optimization <opt>                                ");
        System.out.println("  -O-no-<opt>             : Disable optimization <opt>                               ");
        System.out.println("  -O                      : Disable optimizations, redundant if -O<opt> passed       ");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Where <OPERATION> is one or more of:                                                 ");
        System.out.println("  --lex                   : For each f.(i)xi, generate lex diagnostic file f.lexed   ");
        System.out.println("  --parse                 : For each f.(i)xi, generate parse diagnostic f.(i)parsed  ");
        System.out.println("  --typecheck             : For each f.xi, generate type check diagnostic f.typed    ");
        System.out.println("  --irgen                 : For each f.xi, generate intermediate representation f.ir ");
        System.out.println("  --irrun                 : For each f.xi, generate and run IR f.ir                  ");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Where <FILE> is one or more source files to operate on                               ");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Where <PHASE> is exactly one of:                                                     ");
        System.out.println("  initial : Before any optimizations                                                 ");
        System.out.println("  cf      : Constant folding                                                         ");
        System.out.println("  cp      : Constant propagation                                                     ");
        System.out.println("  cse     : Common subexpression elimination                                         ");
        // System.out.println("  reg     : Register allocation                                                      ");
        // System.out.println("  mc      : Move coalescing                                                          ");
        System.out.println("  final   : After all optimizations                                                  ");
        System.out.println("-------------------------------------------------------------------------------------");
    }

    /**
     * Helper function to display optimization information.
     */
    private static void displayOpts() {
        System.out.println("cf");
        System.out.println("cp");
        System.out.println("cse");
        System.out.println("reg");
        System.out.println("mc");
    }
}
