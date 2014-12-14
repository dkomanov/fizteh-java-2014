package ru.fizteh.fivt.students.sautin1.parallel.storeable;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.parallel.filemap.GeneralTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sautin1 on 12/9/14.
 */
public class StoreableTable extends GeneralTable<Storeable> implements Table {
    private final List<Class<?>> valueTypes;
    private final StoreableTableProvider provider;

    StoreableTable(String name, boolean autoCommit, List<Class<?>> valueTypes, StoreableTableProvider provider) {
        super(name, autoCommit, provider);
        if (valueTypes == null) {
            throw new IllegalArgumentException("Value types are not defined");
        }
        this.valueTypes = Collections.unmodifiableList(new ArrayList<>(valueTypes));
        this.provider = provider;
    }

    StoreableTable(String name, List<Class<?>> valueTypes, StoreableTableProvider provider) {
        this(name, true, valueTypes, provider);
    }

    @Override
    public int getColumnsCount() {
        return valueTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return valueTypes.get(columnIndex);
    }

    public List<Class<?>> getColumnTypes() {
        return valueTypes;
    }
}
