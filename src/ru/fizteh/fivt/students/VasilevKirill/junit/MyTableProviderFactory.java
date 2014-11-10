package ru.fizteh.fivt.students.VasilevKirill.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

/**
 * Created by Kirill on 10.11.2014.
 */
public class MyTableProviderFactory implements TableProviderFactory {
    public MyTableProviderFactory() {

    }

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        MyTableProvider multiMap = new MyTableProvider(dir);
        return multiMap;
    }
}
