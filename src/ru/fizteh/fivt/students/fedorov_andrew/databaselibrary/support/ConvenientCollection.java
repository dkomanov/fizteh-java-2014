package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Usual collection over the given base collection with extension {@link ru.fizteh.fivt.students
 * .fedorov_andrew.databaselibrary.support.ConvenientCollection#addNext
 * (Object)}.
 */
public class ConvenientCollection<T> implements Collection<T> {
    private final Collection<T> collection;

    public ConvenientCollection(Collection<T> base) {
        this.collection = base;
    }

    public ConvenientCollection<T> addNext(T element) {
        collection.add(element);
        return this;
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return collection.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return collection.iterator();
    }

    @Override
    public Object[] toArray() {
        return collection.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return collection.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return collection.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return collection.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return collection.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return collection.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return collection.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return collection.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return collection.retainAll(c);
    }

    @Override
    public void clear() {
        collection.clear();
    }

    @Override
    public Spliterator<T> spliterator() {
        return collection.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return collection.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return collection.parallelStream();
    }

    @Override
    public int hashCode() {
        return collection.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConvenientCollection) {
            return collection.equals(((ConvenientCollection) o).collection);
        }
        return collection.equals(o);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        collection.forEach(action);
    }
}
