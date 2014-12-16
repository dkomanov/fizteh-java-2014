package ru.fizteh.fivt.students.elina_denisova.j_unit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public MyTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("MyTableProvider.create: " + dir
            + " isn't a directory. ");
        }
        return new MyTableProvider(dir);
    }
}
