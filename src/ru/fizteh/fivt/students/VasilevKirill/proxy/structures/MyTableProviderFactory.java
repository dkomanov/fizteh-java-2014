package ru.fizteh.fivt.students.VasilevKirill.proxy.structures;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by Kirill on 10.11.2014.
 */
public class MyTableProviderFactory implements TableProviderFactory {
    private boolean isClosed = false;

    public MyTableProviderFactory() {

    }

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        if (isClosed) {
            throw new IllegalStateException();
        }
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
