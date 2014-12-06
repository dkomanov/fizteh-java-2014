package ru.fizteh.fivt.students.dnovikov.storeable;


import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.LoadOrSaveException;

public class DataBaseProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws LoadOrSaveException {
        return new DataBaseProvider(dir);
    }
}
