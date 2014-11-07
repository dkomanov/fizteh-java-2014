package ru.fizteh.fivt.students.dnovikov.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;

public class DataBaseProviderFactory implements TableProviderFactory{
    @Override
    public TableProvider create(String dir) throws LoadOrSaveException {
        return new DataBaseProvider(dir);
    }
}
