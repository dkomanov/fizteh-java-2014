package ru.fizteh.fivt.students.PotapovaSofia.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DbTableProviderFactory implements TableProviderFactory{
    public DbTableProviderFactory() {

    }
    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new DbTableProvider(dir);
    }
}
