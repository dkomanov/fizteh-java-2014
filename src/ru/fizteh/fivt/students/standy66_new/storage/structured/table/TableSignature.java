package ru.fizteh.fivt.students.standy66_new.storage.structured.table;

import ru.fizteh.fivt.storage.structured.Table;

import java.util.stream.IntStream;

/**
 * Created by andrew on 07.11.14.
 */
public class TableSignature {
    private Class<?>[] storedObjectClasses;

    public TableSignature(Class<?>... storedObjectClasses) {
        for (Class<?> storedObjectClass : storedObjectClasses) {
            if (storedObjectClass == null) {
                throw new IllegalArgumentException("column class should not be null");
            }
        }
        this.storedObjectClasses = storedObjectClasses;
    }

    public static TableSignature forTable(Table t) {
        return new TableSignature(
                IntStream.range(0, t.getColumnsCount())
                        .mapToObj(i -> t.getColumnType(i))
                        .toArray(size -> new Class<?>[size]));
    }

    public Class<?> getClassAt(int columnIndex) throws IndexOutOfBoundsException {
        return storedObjectClasses[columnIndex];
    }

    public int size() {
        return storedObjectClasses.length;
    }
}
