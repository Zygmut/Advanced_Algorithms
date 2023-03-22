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

    @SuppressWarnings("unchecked")
    public LinkedHashQueue<K,V> rest(){
        LinkedHashQueue<K,V> set = (LinkedHashQueue<K,V>) this.clone();
        set.remove(set.peek().getKey());
        return set;
    }

    public LinkedHashQueue<K, V> add(K key, V value){
        this.put(key, value);
        return this;
    }
    public Entry<K,V> peek() {
        return this.entrySet().iterator().next();
    }

}