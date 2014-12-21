package ru.fizteh.fivt.students.RadimZulkarneev.DataBase;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TableProviderFactoryRealize implements TableProviderFactory {

    public TableProviderFactoryRealize() {
    }

    @Override
    public TableProvider create(String dir) {
        nameIsNullAssertion(dir);
        return new TableProviderRealize(dir);
    }

    private void nameIsNullAssertion(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
    }
}
