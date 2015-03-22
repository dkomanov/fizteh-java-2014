package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DBProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        try {
            if (dir == null) {
                throw new Exception();
            }
            return new DBProvider(dir);
        } catch (Exception e) {
            System.err.println("Null Factory");
            System.exit(1);
            return null;
        }
    }
}
