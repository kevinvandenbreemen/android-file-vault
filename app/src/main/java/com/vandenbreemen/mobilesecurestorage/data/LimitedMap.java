package com.vandenbreemen.mobilesecurestorage.data;

import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * <h2>Intro</h2>
 * <p>Map that contains a maximum number of elements
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class LimitedMap<K, V> implements Map<K, V> {
    /**
     * Keys in predictable order
     */
    private List<K> keys;

    /**
     * Backing contents
     */
    private Map<K, V> contents;

    /**
     * Maximum number of elements allowed in the map
     */
    private int maxSize;

    /**
     * Obtains a thread-safe version of this map
     *
     * @param maxSize
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> LimitedMap<K, V> concurrentMap(int maxSize) {
        LimitedMap<K, V> ret = new LimitedMap<>(maxSize);
        ret.contents = new ConcurrentHashMap<>();
        ret.keys = new Vector<>();
        return ret;
    }

    /**
     * Obtains a thread-safe version of this map
     *
     * @param maxSize
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> LimitedMap<K, V> concurrentMap(int maxSize, BiFunction<Object, Object, Void> onEviction) {
        LimitedMap<K, V> ret = new LimitedMap<K, V>(maxSize) {
            @Override
            protected void onEvict(Object key, Object value) {
                onEviction.apply(key, value);
            }
        };
        ret.contents = new ConcurrentHashMap<>();
        ret.keys = new Vector<>();
        return ret;
    }

    public LimitedMap(int maxSize) {
        this.maxSize = maxSize;
        this.contents = new HashMap<>();
        this.keys = new LinkedList<>();
    }

    @Override
    public void clear() {
        if (!MapUtils.isEmpty(contents)) {

            List<K> keySet = new LinkedList<>(contents.keySet());

            keySet.forEach(key -> {
                V value = contents.remove(key);
                onEvict(key, value);
            });
        }

        this.keys = new LinkedList<>();
    }

    /**
     * Action to be taken on eviction of a mapping
     *
     * @param key
     * @param value
     */
    protected void onEvict(Object key, V value) {
        //  To be overridden by subtypes
    }

    @Override
    public boolean containsKey(Object key) {
        return contents.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return contents.containsValue(value);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return contents.entrySet();
    }

    @Override
    public V get(Object key) {
        return contents.get(key);
    }

    @Override
    public boolean isEmpty() {
        return contents.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return contents.keySet();
    }

    @Override
    public V put(K key, V value) {

        //	If max size exceeded then evict the first element
        if (contents.size() >= maxSize) {
            K toRemove = this.keys.remove(0);    //	Remove from the tail of the queue
            remove(toRemove);
        }

        this.keys.add(key);    //	Add the new key to the end of the queue
        return this.contents.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new MSSRuntime("Insertion of maps not supported");
    }

    //	Depending on the implementation of onEvict() returned result may not be usable!
    @Override
    public V remove(Object key) {
        if (key == null)
            throw new MSSRuntime("Key cannot be null during a removal");
        V removed = contents.remove(key);
        keys.remove(key);
        onEvict(key, removed);
        return removed;
    }

    @Override
    public int size() {
        return contents.size();
    }

    @Override
    public Collection<V> values() {
        return contents.values();
    }
}
