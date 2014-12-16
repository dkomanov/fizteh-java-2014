package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider.utils.SyntaxCheckers;

import java.nio.file.Paths;

public class DbManagerFactory implements TableProviderFactory {
    @Override
    public TableProvider create(final String dir) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(dir)) {
            throw new IllegalArgumentException("wrong tableprovider dir: " + dir);
        } else {
            try {
                return new DbTableProvider(Paths.get(dir));
            } catch (RuntimeException e) {
                throw new RuntimeException("can't load database: " + e.getMessage());
            }
        }
    }
}
