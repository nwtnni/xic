package xic.phase;

import util.Result;

abstract class Phase {
    
    protected boolean output;

    public Phase() { output = false; }

    public void setOutput() { output = true; }

    public abstract Result<Intermediate> process(Result<Intermediate> previous);

}
