package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.nio.file.Paths;

public class MultiFileTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        try {
            return new MultiFileTableProvider(Paths.get("").resolve(Paths.get(dir)));
        } catch (ConnectionInterruptException e) {
            return null;
        }
    }
}
