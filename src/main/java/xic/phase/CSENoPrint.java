package xic.phase;

public class CSENoPrint extends CSE {

    public CSENoPrint() { kind = Phase.Kind.CSE; output = false; }

    @Override
    public void setOutput() { this.output = false; }

    @Override
    public void setOutputCFG() { this.outputCFG = false; }

}
