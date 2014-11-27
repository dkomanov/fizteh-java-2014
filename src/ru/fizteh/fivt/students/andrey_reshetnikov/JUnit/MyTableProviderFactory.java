package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory{

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        return new MyTableProvider(dir);
    }
}
