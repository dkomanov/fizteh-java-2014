package ru.fizteh.fivt.students.gudkov394.Junit.src;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class Junit implements TableProviderFactory {
    /*public static void main(final String[] args) {
    }*/

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        String[] argsForCreate = new String[0];
        System.setProperty("db.file", dir);
        tableProviderClass tableProviderClass = new tableProviderClass();
        return tableProviderClass;
    }
}
