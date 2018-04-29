package xic.phase;

public class ConstPropNoPrint extends ConstProp {

    public ConstPropNoPrint() { kind = Phase.Kind.CONSTPROP; output = false; }

    @Override
    public void setOutput() { this.output = false; }

    @Override
    public void setOutputCFG() { this.outputCFG = false; }

}
