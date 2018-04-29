package xic.phase;

public class FoldNoPrint extends Fold {

    public FoldNoPrint() { kind = Phase.Kind.FOLD; output = false; }

    @Override
    public void setOutput() { this.output = false; }

    @Override
    public void setOutputCFG() { this.outputCFG = false; }

}
