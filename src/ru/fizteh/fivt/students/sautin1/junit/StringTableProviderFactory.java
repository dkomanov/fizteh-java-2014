package ru.fizteh.fivt.students.sautin1.junit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.sautin1.junit.filemap.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Factory for StringTable provider.
 * Created by sautin1 on 10/28/14.
 */
public class StringTableProviderFactory implements TableProviderFactory {
    public StringTableProvider create(Path dirPath, boolean autoCommit, StringTableIOTools ioTools) {
        return new StringTableProvider(dirPath, autoCommit, ioTools);
    }

    public StringTableProvider create(String dir, boolean autoCommit, StringTableIOTools ioTools) {
        if (dir == null) {
            throw new IllegalArgumentException("No directory provided");
        }
        Path rootDir = Paths.get(dir);
        return create(rootDir, autoCommit, ioTools);
    }

    public StringTableProvider create(String dir, boolean autoCommit) {
        return create(dir, autoCommit, new StringTableIOToolsMultipleFiles(16, 16, "UTF-8"));
    }

    public StringTableProvider create(String dir) {
        StringTableProvider tableProvider;
        tableProvider = create(dir, false);
        return tableProvider;
    }
}
