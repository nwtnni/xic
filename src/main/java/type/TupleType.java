package type;

import java.util.List;
import java.util.ArrayList;

public class TupleType extends Type {

    private List<Type> tuple;

    public TupleType(List<Type> tuple) {
        this.tuple = new ArrayList<>(tuple);
    }

    @Override
    public boolean isTuple() { return true; }

    public List<Type> getTuple() {
        return new ArrayList<>(tuple);
    }

    public int size() {
        return tuple.size();
    }

}
