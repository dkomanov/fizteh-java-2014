package ru.fizteh.fivt.students.pershik.Proxy;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.pershik.Storeable.StoreableEntry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pershik on 11/28/14.
 */
public class MyStoreableEntry extends StoreableEntry implements Storeable {
    public MyStoreableEntry(List<Class<?>> newSignature) {
        super(newSignature);
    }

    @Override
    public String toString() {
        return "MyStoreableEntry"
                + this.list.stream().map(
                        (object) -> object == null ? "" : object.toString()).
                        collect(Collectors.joining(",", "[", "]"));
    }
}
