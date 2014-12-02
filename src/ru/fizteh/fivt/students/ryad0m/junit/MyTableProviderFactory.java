package ru.fizteh.fivt.students.ryad0m.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Paths;

public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        return new MyTableProvider(Paths.get(dir).toAbsolutePath().normalize());
    }
}
