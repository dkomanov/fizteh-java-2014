package ru.fizteh.fivt.students.ekaterina_pogodina.JUnit;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.TableProvider;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.TableProviderFactory;

public class DataBaseTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws Exception {
        return new DBaseTableProvider(dir);
    }
}
