package ru.fizteh.fivt.students.anastasia_ermolaeva.junit;


import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TableHolderFactory implements TableProviderFactory {

    @Override
    public final TableProvider create(final String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new TableHolder(dir);
    }
}
