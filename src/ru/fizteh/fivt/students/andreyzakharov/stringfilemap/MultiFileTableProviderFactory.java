package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

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
