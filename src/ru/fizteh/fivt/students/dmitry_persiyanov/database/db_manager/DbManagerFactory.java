package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.utils.SyntaxCheckers;

import java.io.File;
import java.io.IOException;

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
                return new DbManager(dirPath);
            } catch (RuntimeException e) {
                throw new RuntimeException("can't load database: " + e.getMessage());
            }
        }
    }
}
