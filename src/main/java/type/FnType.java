package type;

import java.util.ArrayList;

import ast.*;

public class FnType {

    public ArrayList<Type> args;
    public ArrayList<Type> returns;

    public FnType(Fn f) {

        ArrayList<Type> args = new ArrayList<>(); 
        ArrayList<Type> returns = new ArrayList<>();

        for (Node arg : f.args) {
            args.add(new Type((XiType) arg));
        }

        if (f.isFn()) {
            for (Node type : f.returns) {
                returns.add(new Type((XiType) type));
            }
        }
    }
}
