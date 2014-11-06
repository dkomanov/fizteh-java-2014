package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMap;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by moskupols on 06.11.14.
 */
public class MultiFileMapTableAdaptor implements KnownDiffTable {
    protected final MultiFileMap delegate;
    protected Map<String, String> putProxy;
    protected Set<String> newKeysProxy;
    protected Map<String, String> removeProxy;

    MultiFileMapTableAdaptor(MultiFileMap delegate) {
        this.delegate = delegate;
        putProxy = new HashMap<>();
        newKeysProxy = new HashSet<>();
        removeProxy = new HashMap<>();
    }

    protected final String getFromDelegate(String key) {
        try {
            return delegate.get(key);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key shouldn't be null");
        }
        if (removeProxy.containsKey(key)) {
            return null;
        }
        String ret = putProxy.get(key);
        return ret != null ? ret : getFromDelegate(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("key and value should both be not null");
        }
        String ret = putProxy.put(key, value);
        String removedValue = removeProxy.remove(key);
        if (ret == null) {
            ret = removedValue;
        }
        if (ret == null) {
            ret = getFromDelegate(key);
            newKeysProxy.add(key);
        }
        return ret;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key shouldn't be null");
        }
        String ret = removeProxy.get(key);
        if (ret != null) {
            return ret;
        }
        ret = putProxy.remove(key);
        if (ret != null) {
            if (!newKeysProxy.remove(key)) {
                removeProxy.put(key, ret);
            }
        } else {
            ret = getFromDelegate(key);
            if (ret != null) {
                removeProxy.put(key, ret);
            }
        }
        return ret;
    }

    @Override
    public int size() {
        return delegate.size() + newKeysProxy.size() - removeProxy.size();
    }

    @Override
    public List<String> list() {
        List<String> ret;
        try {
            ret = delegate.list().stream()
                    .filter(k -> !removeProxy.containsKey(k))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        ret.addAll(newKeysProxy);
        return ret;
    }

    @Override
    public int commit() {
        int ret = diff();
        try {
            for (Map.Entry<String, String> entry : putProxy.entrySet()) {
                delegate.put(entry.getKey(), entry.getValue());
            }
            for (String k : removeProxy.keySet()) { // we can't use foreach here, because remove is unsafe.
                delegate.remove(k);
            }
            delegate.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return ret;
    }

    @Override
    public int rollback() {
        int ret = diff();
        newKeysProxy.clear();
        putProxy.clear();
        removeProxy.clear();
        return ret;
    }

    @Override
    public int diff() {
        return putProxy.size() + removeProxy.size();
    }
}
