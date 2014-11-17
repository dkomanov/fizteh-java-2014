package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Paths;

/**
 * Created by moskupols on 17.11.14.
 */
public class MultiFileMapTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        return new MultiFileMapTableProvider(Paths.get(dir));
    }
}
