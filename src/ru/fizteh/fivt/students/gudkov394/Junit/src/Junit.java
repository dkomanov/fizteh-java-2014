package ru.fizteh.fivt.students.gudkov394.Junit.src;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class Junit implements TableProviderFactory {
    /*public static void main(final String[] args) {
    }*/

    @Override
    public TableProviderClass create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        String[] argsForCreate = new String[0];
        System.setProperty("fizteh.db.dir", dir);
        TableProviderClass tableProviderClass = new TableProviderClass();
        return tableProviderClass;
    }
}
