package xic.phase;

import util.Result;

public class Parse extends Phase {

    public Parse() { kind = Phase.Kind.PARSE; }
    
    @Override
    public Result<Intermediate> process(Config config, Result<Intermediate> previous) {

        if (previous.isErr() && output) {
            

        }


        if (output) {
            String output = Filename.concat(sink, unit);

        }

        return null;
    }

}
