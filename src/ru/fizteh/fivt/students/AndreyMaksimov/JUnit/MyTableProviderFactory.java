package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.strings.TableProvider;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        return new MyTableProvider(dir);
    }
}