package xic.phase;

import util.Result;

public class Parse extends Phase {

    public Parse() { kind = Phase.Kind.PARSE; }
    
    @Override
    public Result<Intermediate> process(Result<Intermediate> previous) {
        //TODO
        return null;
    }

}