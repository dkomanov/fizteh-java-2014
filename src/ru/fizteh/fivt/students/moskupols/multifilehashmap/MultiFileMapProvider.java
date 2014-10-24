package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

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
        try {
            Files.createDirectory(tablePath);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Couldn't create %s", tablePath));
        }
        return new MultiFileMap(tablePath);
    }

    public boolean removeTable(String name) {
        final MultiFileMap table = getTable(name);
        if (table != null) {
            table.clear();
            final Path tablePath = rootPath.resolve(name);
            try {
                Files.delete(tablePath);
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Couldn't delete %s", tablePath));
            }
            return true;
        }
        return false;
    }

    public List<String> listNames() {
        return Arrays.asList(rootPath.toFile().list());
    }
}
