package ru.fizteh.fivt.students.LebedevAleksey.junit;

import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.DatabaseFileStructureException;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.LoadOrSaveException;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.TableAlreadyExistsException;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.TableNotFoundException;

public class StringTableProvider implements ru.fizteh.fivt.storage.strings.TableProvider {
    protected Database database;

    public StringTableProvider(Database database) {
        this.database = database;
    }

    private void checkNameNotNull(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument \"name\" is null");
        }
    }

    @Override
    public ru.fizteh.fivt.storage.strings.Table getTable(String name) {
        checkNameNotNull(name);
        try {
            return new StringTable((Table) database.getTable(name));
        } catch (TableNotFoundException e) {
            return null;
        }
    }

    @Override
    public ru.fizteh.fivt.storage.strings.Table createTable(String name) {
        checkNameNotNull(name);
        try {
            return new StringTable((Table) database.createTable(name));
        } catch (TableAlreadyExistsException e) {
            return null;
        } catch (LoadOrSaveException | DatabaseFileStructureException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void removeTable(String name) {
        checkNameNotNull(name);
        try {
            database.removeTable(name);
        } catch (TableNotFoundException e) {
            throw new IllegalStateException(name + " not exist");
        } catch (LoadOrSaveException | DatabaseFileStructureException e) {
            throw new DatabaseException(e);
        }
    }
}
