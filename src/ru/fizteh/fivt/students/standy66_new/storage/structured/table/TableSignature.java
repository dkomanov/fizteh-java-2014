package ru.fizteh.fivt.students.standy66_new.storage.structured.table;

/**
 * Created by andrew on 07.11.14.
 */
public class TableSignature {
    private Class<?>[] storedObjectClasses;

    public TableSignature(Class<?>... storedObjectClasses) {
        //TODO: check if some of them is null
        this.storedObjectClasses = storedObjectClasses;
    }

    public Class<?> getClassAt(int columnIndex) throws IndexOutOfBoundsException {
        return storedObjectClasses[columnIndex];
    }

    public int size() {
        return storedObjectClasses.length;
    }
}
