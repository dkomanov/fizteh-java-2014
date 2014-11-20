package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.SyntaxCheckers;

import java.io.File;

/**
 * Created by drack3800 on 12.11.2014.
 */

public class DbManagerFactory implements TableProviderFactory {
    @Override
    public TableProvider create(final String dir) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(dir)) {
            throw new IllegalArgumentException("wrong tableprovider dir: " + dir);
        } else {
            File dirPath = new File(dir);
            try {
                return new DbTableProvider(dirPath);
            } catch (RuntimeException e) {
                throw new RuntimeException("can't load database: " + e.getMessage());
            }
        }
    }
}
