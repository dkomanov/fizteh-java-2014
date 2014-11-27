package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.io.FileNotFoundException;
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

    public MultiFileMapProvider(Path rootPath) throws IOException {
        this.rootPath = rootPath;

        if (!Files.exists(rootPath)) {
            throw new FileNotFoundException(String.format("DB directory %s does not exist", rootPath));
        }
        if (!Files.isDirectory(rootPath)) {
            throw new IOException(String.format("%s is not directory", rootPath));
        }
    }

    public MultiFileMap getTable(String name) throws IOException {
        return openTable(name);
    }

    public MultiFileMap createTable(String name) throws IOException {
        final Path tablePath = rootPath.resolve(name);
        if (Files.exists(tablePath)) {
            return null;
        }
        try {
            Files.createDirectory(tablePath);
        } catch (IOException e) {
            throw new IOException(String.format("Couldn't create %s", tablePath), e);
        }
        return new MultiFileMap(tablePath);
    }

    public boolean removeTable(String name) throws IOException {
        final MultiFileMap table = openTable(name);
        if (table != null) {
            table.clear();
            final Path tablePath = rootPath.resolve(name);
            try {
                Files.delete(tablePath);
            } catch (IOException e) {
                throw new IOException("Couldn't delete " + tablePath, e);
            }
            return true;
        }
        return false;
    }

    public List<String> listNames() {
        return Arrays.asList(rootPath.toFile().list());
    }

    private MultiFileMap openTable(String name) throws IOException {
        final Path tablePath = rootPath.resolve(name);
        if (!Files.exists(tablePath)) {
            return null;
        }
        return new MultiFileMap(tablePath);
    }
}
