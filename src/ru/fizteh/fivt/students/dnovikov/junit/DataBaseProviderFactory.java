package ru.fizteh.fivt.students.dnovikov.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DataBaseProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        return new DataBaseProvider(dir);
    }
}
