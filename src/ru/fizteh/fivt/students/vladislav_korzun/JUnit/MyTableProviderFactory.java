package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory{

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        try {
            return new MyTableProvider(dir);
        } catch (IllegalArgumentException e) {
            throw new DataBaseException(e.getMessage());
        }
    }    

}
