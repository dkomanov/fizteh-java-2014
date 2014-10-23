package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by moskupols on 23.10.14.
 */
public class MultiFileMapProvider {

    private final Path rootPath;

    public MultiFileMapProvider(Path rootPath) {
        this.rootPath = rootPath;

        if (!Files.exists(rootPath))
            throw new IllegalStateException(String.format("DB directory %s does not exist", rootPath));
        if (!Files.isDirectory(rootPath))
            throw new IllegalStateException(String.format("%s is not directory", rootPath));
    }

    public MultiFileMap getTable(String name) {
        final Path tablePath = rootPath.resolve(name);
        if (!Files.exists(tablePath))
            return null;
        return new MultiFileMap(tablePath);
    }

    public MultiFileMap createTable(String name) {
        final Path tablePath = rootPath.resolve(name);
        if (Files.exists(tablePath))
            return null;
        return new MultiFileMap(tablePath);
    }

    public void removeTable(String name) {
        final MultiFileMap table = getTable(name);
        if (table != null)
            table.clear();
    }
}
