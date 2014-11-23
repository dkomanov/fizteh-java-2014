package ru.fizteh.fivt.students.SurkovaEkaterina.Storable.TableSystem;

import java.util.ArrayList;
import java.util.List;

public class TableInfo {
    private String tableName;
    private List<Class<?>> columnTypes;

    public TableInfo(String tableName) {
        this.columnTypes = new ArrayList<Class<?>>();
        this.tableName = tableName;
    }

    public void addColumn(Class<?> columnType) {
        columnTypes.add(columnType);
    }

    public List<Class<?>> getColumnTypes() {
        return columnTypes;
    }

    public String getTableName() {
        return tableName;
    }
}
