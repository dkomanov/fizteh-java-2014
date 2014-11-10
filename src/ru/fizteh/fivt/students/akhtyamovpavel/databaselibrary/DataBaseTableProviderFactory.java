package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

/**
 * Created by user1 on 21.10.2014.
 */
public class DataBaseTableProviderFactory implements TableProviderFactory {

    @Override
    public DataBaseTableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("null pointer");
        }
        try {
            return new DataBaseTableProvider(dir);
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad database directory");
        }
    }

    public DataBaseTableProvider create(String dir, boolean testMode) throws Exception {
        return new DataBaseTableProvider(dir, testMode);
    }
}
