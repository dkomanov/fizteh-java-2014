package ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage;

import ru.fizteh.fivt.storage.structured.Storeable;

public class AbstractStoreable implements Storeable {
    private Object[] values;

    public AbstractStoreable(Object[] newValues) {
        values = newValues;
    }
}
