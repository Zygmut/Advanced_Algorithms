package Chess;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CircularLinkedHashMap<K, V> implements Iterator<Map.Entry<K, V>> {
    private final LinkedHashMap<K, V> map;
    private Iterator<Map.Entry<K, V>> iterator;

    public CircularLinkedHashMap(LinkedHashMap<K, V> map) {
        this.map = map;
        this.iterator = map.entrySet().iterator();
    }

    public CircularLinkedHashMap(){
        this.map = new LinkedHashMap<>();
        this.iterator = map.entrySet().iterator();
    }


    @Override
    public boolean hasNext() {
        return true; // always return true for circular iteration
    }

    @Override
    public Map.Entry<K, V> next() {
        if (!iterator.hasNext()) {
            iterator = map.entrySet().iterator(); // wrap around to the beginning
        }
        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    public LinkedHashMap<K, V> getMap() {
        return map;
    }

    public Iterator<Map.Entry<K, V>> getIterator() {
        return iterator;
    }
}