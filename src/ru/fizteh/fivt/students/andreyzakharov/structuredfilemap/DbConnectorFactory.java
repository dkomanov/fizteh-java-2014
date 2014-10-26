package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Paths;

public class DbConnectorFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        try {
            return new DbConnector(Paths.get("").resolve(Paths.get(dir)));
        } catch (ConnectionInterruptException e) {
            return null;
        }
    }
}
