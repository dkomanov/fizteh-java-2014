package ru.fizteh.fivt.students.PotapovaSofia.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class DbTableProviderFactory implements TableProviderFactory{
    public DbTableProviderFactory() {

    }
    @Override
    public TableProvider create(String dir) throws IOException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new DbTableProvider(dir);
    }
}
