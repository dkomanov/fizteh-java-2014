package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ВАНЯ on 20.12.2014.
 */
public class StoreableEntry extends CurrentStoreable implements Storeable {

    public StoreableEntry(List<Class<?>> newSignature) {
        super(newSignature);
    }

    @Override
    public String toString() {
        return "MyStoreableEntry"
                + this.getValues().stream().map(
                (object) -> object == null ? "" : object.toString()).
                collect(Collectors.joining(",", "[", "]"));
    }

}
