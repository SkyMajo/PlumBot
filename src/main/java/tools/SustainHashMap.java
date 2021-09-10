package tools;

import java.util.HashMap;

public class SustainHashMap<K,V> extends HashMap<K,V> {

    @Override
    public V put(K key, V value) {
        if (get(key)!=null) {
            remove(key);
            return super.put(key, value);
        }else{
            return super.put(key, value);

        }
    }


}
