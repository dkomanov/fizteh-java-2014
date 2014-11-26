package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Usual map extended with method {@link ConvenientMap#putNext(Object, Object)}.
 */
public class ConvenientMap<K, V> implements Map<K, V> {
    private final Map<K, V> baseMap;

    public ConvenientMap(Map<K, V> baseMap) {
        this.baseMap = baseMap;
    }

    public ConvenientMap<K, V> putNext(K key, V value) {
        baseMap.put(key, value);
        return this;
    }

    @Override
    public int size() {
        return baseMap.size();
    }

    @Override
    public boolean isEmpty() {
        return baseMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return baseMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return baseMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return baseMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return baseMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return baseMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        baseMap.putAll(m);
    }

    @Override
    public void clear() {
        baseMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return baseMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return baseMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return baseMap.entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return baseMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        baseMap.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        baseMap.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return baseMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return baseMap.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return baseMap.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return baseMap.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return baseMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return baseMap.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return baseMap.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return baseMap.merge(key, value, remappingFunction);
    }

    @Override
    public int hashCode() {
        return baseMap.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConvenientMap) {
            return baseMap.equals(((ConvenientMap) o).baseMap);
        }
        return baseMap.equals(o);
    }
}
