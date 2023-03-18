package Chess;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LinkedHashQueue<K, V> extends LinkedHashMap<K,V> {

    public LinkedHashQueue(){
        super();
    }

    public Entry<K,V> next() {
        Entry<K,V> next = this.peek();
        this.remove(next.getKey());
        return next;

    }

    public Entry<K,V> peek() {
        return this.entrySet().iterator().next();
    }

}