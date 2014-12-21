package ru.fizteh.fivt.students.PotapovaSofia.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.PotapovaSofia.JUnit.Interpreter.Utils;

public class DbTableProviderFactory implements TableProviderFactory{
    public DbTableProviderFactory() {

    }
    @Override
    public TableProvider create(String dir) {
        Utils.checkOnNull(dir, "Directory name is null");
        /*
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        */
        return new DbTableProvider(dir);
    }
}
