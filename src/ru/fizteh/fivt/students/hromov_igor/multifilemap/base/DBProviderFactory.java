package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DBProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws Exception {
        if (dir == null) {
            throw new Exception();
        }
        return new DBProvider(dir);
    }
}