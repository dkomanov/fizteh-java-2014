package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase;


import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by user1 on 21.10.2014.
 */
public class DataBaseTableProviderFactory implements TableProviderFactory {

    @Override
    public DataBaseTableProvider create(String path) throws IOException{
        if (path == null) {
            throw new IllegalArgumentException("Null table path");
        }
        try {
            return new DataBaseTableProvider(path);
        } catch (Exception e) {
            return null;
        }
    }

    public DataBaseTableProvider create(String dir, boolean testMode) throws Exception {
        return new DataBaseTableProvider(dir, testMode);
    }
}
