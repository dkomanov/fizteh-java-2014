package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;

public class DBFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        try {
            return new DBTableProvider(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
