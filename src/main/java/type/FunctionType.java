package type;

import java.util.ArrayList;

import ast.*;

public class FunctionType {

    public ArrayList<Type> args;
    public ArrayList<Type> returns;

    public FunctionType(Function f) {

        ArrayList<Type> args = new ArrayList<>(); 
        ArrayList<Type> returns = new ArrayList<>();

        for (Node arg : f.args) {
            args.add(new Type((XiType) arg));
        }

        if (f.isFunction()) {
            for (Node type : f.types) {
                returns.add(new Type((XiType) type));
            }
        }
    }
}
