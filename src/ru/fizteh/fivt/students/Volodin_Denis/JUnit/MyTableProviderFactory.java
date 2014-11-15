package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("wrong directory");
        }
        
        try {
            return new MyTableProvider(dir);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
