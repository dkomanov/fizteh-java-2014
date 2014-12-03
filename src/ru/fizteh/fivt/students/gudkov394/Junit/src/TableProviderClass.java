package ru.fizteh.fivt.students.gudkov394.Junit.src;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kagudkov on 25.10.14.
 */
public class TableProviderClass  implements TableProvider{
    public Map<String, CurrentTable> tables = new HashMap<String, CurrentTable>();

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!tables.containsKey(name)) {
            return null;
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            CurrentTable newTable = new CurrentTable(name);
            newTable.create();
            tables.put(name, newTable);
            return newTable;
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            tables.get(name).delete();
            tables.remove(name);
        } else {
            throw new IllegalStateException();
        }
    }

    public void put(String tmp, CurrentTable ct) {
        tables.put(tmp, ct);
    }
}
