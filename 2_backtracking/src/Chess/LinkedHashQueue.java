package Chess;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LinkedHashQueue<K, V> {
    private final Map<K, V> map;

    public LinkedHashQueue(LinkedHashMap<K, V> map) {
        this.map = map;
    }

    public LinkedHashQueue(){
        this.map = new LinkedHashMap<>();
    }

    public void add(K key, V value) {
        map.put(key, value);
    }

    public Entry<K,V> next() {
        Entry<K,V> next = this.peek();
        map.remove(map.entrySet().iterator().next().getKey());
        return next;

    }

    public Entry<K,V> peek() {
        return map.entrySet().iterator().next();
    }

    public Map<K, V> getMap() {
        return map;
    }

}