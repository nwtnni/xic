package util;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class OrderedMap<K, V> {
    
    private Map<K, V> map;
    private List<K> order;

    public OrderedMap() {
        this.map = new HashMap<>();
        this.order = new ArrayList<>();
    }

    public List<K> keyList() {
        return new ArrayList<>(order);
    }

    public V get(K key) {
        return map.get(key);
    }

    public Pair<K, V> get(int index) {
        K key = order.get(index);
        return new Pair<>(key, map.get(key));
    }

    public int indexOf(K key) {
        return order.indexOf(key);
    }

    public void put(K key, V value) {
        map.put(key, value);
        order.add(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public void addAll(OrderedMap<K, V> other) {
        map.putAll(other.map);
        order.addAll(other.order);
    }

    public int size() {
        return order.size();
    }
}
