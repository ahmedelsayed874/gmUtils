package gmutils.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gmutils.listeners.ActionCallback;
import gmutils.listeners.ActionCallback2;
import gmutils.listeners.ResultCallback;


public class MapWrapper<K, V> {

    public static <K, V> MapWrapper<K, V> create(Class<K> kClass, Class<V> vClass) {
        return new MapWrapper<>(null);
    }

    public static <K, V> MapWrapper<K, V> create(Map<K, V> map) {
        return new MapWrapper<>(map);
    }

    //----------------------------------------------------------------------------------------------

    private final Map<K, V> map;

    private MapWrapper(Map<K, V> map) {
        if (map == null) {
            this.map = new HashMap<>();
        } else {
            this.map = map;
        }
    }

    //region add methods
    public MapWrapper<K, V> add(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public MapWrapper<K, V> add(Map<K, V> otherMap) {
        this.map.putAll(otherMap);
        return this;
    }

    public MapWrapper<K, V> add(List<K> keys, List<V> value) {
        if (keys == null || value == null || keys.size() != value.size())
            throw new IllegalArgumentException("Keys must has corresponding Values");

        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), value.get(i));
        }

        return this;
    }

    public MapWrapper<K, V> add(List<DataGroup.Two<K, V>> items) {
        if (items == null) return this;

        for (DataGroup.Two<K, V> item : items) {
            map.put(item.value1, item.value2);
        }

        return this;
    }

    public MapWrapper<K, V> add(List<K> keys, ActionCallback<K, V> adder) {
        if (keys == null || keys.size() == 0 || adder == null) return this;

        for (K key : keys) {
            V value = adder.invoke(key);
            add(key, value);
        }

        return this;
    }
    //endregion

    //region remove methods
    public MapWrapper<K, V> remove(K key) {
        this.map.remove(key);
        return this;
    }

    public MapWrapper<K, V> remove(ActionCallback2<K, V, Boolean> remover) {
        if (remover == null) return this;

        LinkedList<K> keysToRemove = new LinkedList<>();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            Boolean rem = remover.invoke(entry.getKey(), entry.getValue());
            if (rem == null) rem = false;

            if (rem)
                keysToRemove.add(entry.getKey());
        }

        for (K key : keysToRemove) {
            map.remove(key);
        }

        return this;
    }
    //endregion

    //region processing methods
    public MapWrapper<K, V> filter(ActionCallback2<K, V, Boolean> action, ResultCallback<Map<K, V>> result) {
        if (result == null) throw new IllegalArgumentException();
        Map<K, V> newMap = filter(action);
        result.invoke(newMap);
        return this;
    }

    public Map<K, V> filter(ActionCallback2<K, V, Boolean> action) {
        if (action == null) return null;

        LinkedList<K> keysTobeSelected = new LinkedList<>();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            Boolean selected = action.invoke(entry.getKey(), entry.getValue());
            if (selected == null) selected = false;
            if (selected) {
                keysTobeSelected.addLast(entry.getKey());
            }
        }

        Map<K, V> map = new HashMap<>();

        for (K key : keysTobeSelected) {
            V value = map.get(key);
            map.put(key, value);
        }

        return map;
    }

    public <Vn> MapWrapper<K, V> map(ActionCallback2<K, V, Vn> action, ResultCallback<Map<K, Vn>> result) {
        if (result == null) throw new IllegalArgumentException();

        Map<K, Vn> map = map(action);
        result.invoke(map);

        return this;
    }

    public <Vn> Map<K, Vn> map(ActionCallback2<K, V, Vn> action) {
        if (action == null) throw new IllegalArgumentException();

        Map<K, Vn> outMap = new HashMap<>();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            Vn newValue = action.invoke(entry.getKey(), entry.getValue());
            outMap.put(entry.getKey(), newValue);
        }

        return outMap;
    }

    public MapWrapper<K, V> performAction(ActionCallback<MapWrapper<K, V>, Void> action) {
        if (action != null)
            action.invoke(this);
        return this;
    }
    //endregion

    public Map<K, V> getMap() {
        return map;
    }
}
