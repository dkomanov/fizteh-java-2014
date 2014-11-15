package ru.fizteh.fivt.students.VasilevKirill.Storeable.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.MultiMap;

import java.io.IOException;

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
        try {
            MultiMap multiMap = new MultiMap(dir);
            return multiMap;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
