package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

/**
 * Created by user1 on 21.10.2014.
 */
public class DataBaseTableProviderFactory implements TableProviderFactory {


    public DataBaseTableProvider create(String dir) {
        return new DataBaseTableProvider(dir);
    }

    public DataBaseTableProvider create(String dir, boolean testMode) throws Exception {
        return new DataBaseTableProvider(dir, testMode);
    }
}
