package util;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class OrderedMap<K, V> {
    
    private Map<K, V> map;
    private List<K> order;

    public Pair<K, V> get(int index) {
        K key = order.get(index);
        return new Pair<>(key, map.get(key));
    }

    public int size() {
        return order.size();
    }
}
