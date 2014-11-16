package ru.fizteh.fivt.students.valentine_lebedeva;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Table {
    protected Map<String, String> base;

    public Table() throws IOException {
        base = new HashMap<>();
    }

    public abstract Map<String, String> read() throws IOException;

    public abstract void write() throws IOException;

    public abstract void close() throws IOException;

    public final Map<String, String> getBase() {
        return base;
    }

    public final void putBase(final String key, final String value) {
        base.put(key, value);
    }

    public final void removeBase(final String key) {
        base.remove(key);
    }

    public final void setBase(Map<String, String> newBase) {
        base = newBase;
    }
}
