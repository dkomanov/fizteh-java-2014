package ru.fizteh.fivt.students.LebedevAleksey.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.DatabaseFileStructureException;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.LoadOrSaveException;

public class StringTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Argument \"dir\" is null");
        }
        try {
            return new StringTableProvider(new Database(dir));
        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
            throw new DatabaseException(e);
        }
    }
}
