package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;

public class DBFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        try {
            if (dir == null) {
                throw new IllegalArgumentException("name can't be null");
            } else {
                return new DBTableProvider(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("creating of database failed");
        }
    }
}
