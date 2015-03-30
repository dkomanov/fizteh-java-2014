package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DBProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {

        if (dir == null) {
            throw new IllegalArgumentException();
        }
        return new DBProvider(dir);
    }
}
