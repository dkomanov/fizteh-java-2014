/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.AbstractTable;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.TableReader;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.TableWriter;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author shakarim
 */
public class MultiFileTable extends AbstractTable {

    private final Path directoryPath;

    public MultiFileTable(String directory, String tableName) throws IOException {
        super(tableName);
        directoryPath = Paths.get(directory, tableName);
        if (!Files.exists(directoryPath)) {
            Files.createDirectory(directoryPath);
        } else {
            load();
        }
    }

    private void load() throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath);
        for (Path file : directoryStream) {
            if (!Files.isDirectory(file)) {
                String format = "%s: Table directory contains illegal files";
                throw new IOException(String.format(format, getName()));
            }
            readDir(file);
        }
    }

    @Override
    protected void save() throws IOException {
        ShellUtils utils = new ShellUtils();
        utils.rm(directoryPath.toString(), true);
        Files.createDirectory(directoryPath);

        TableWriter[][] writers = new TableWriter[16][16];

        try {
            for (Entry<String, String> entry : table.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                int hashCode = Math.abs(key.hashCode());
                int nDir = hashCode % 16;
                int nFile = hashCode / 16 % 16;

                Path subDirectoryPath = getDirectoryPath(key);
                Path filePath = getFilePath(key);

                if (!Files.exists(subDirectoryPath)) {
                    Files.createDirectory(subDirectoryPath);
                }

                if (writers[nDir][nFile] == null) {
                    writers[nDir][nFile] = new TableWriter(filePath.toString());
                }

                writers[nDir][nFile].write(key);
                writers[nDir][nFile].write(value);
            }
        } finally {
            for (int dir = 0; dir < 16; ++dir) {
                for (int file = 0; file < 16; ++file) {
                    if (writers[dir][file] != null) {
                        writers[dir][file].close();
                    }
                }
            }
        }
    }

    private void readDir(Path directory) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory);
        for (Path path : directoryStream) {
            String fileName = path.toString();
            try (TableReader reader = new TableReader(fileName)) {
                String key;
                String value;

                while (!reader.eof()) {
                    key = reader.read();
                    value = reader.read();

                    Path subDirectoryPath = getDirectoryPath(key);
                    Path filePath = getFilePath(key);

                    if (directory.compareTo(subDirectoryPath) != 0
                            || path.compareTo(filePath) != 0) {
                        String format = "%s: contains wrong key";
                        String eMassage = String.format(format, fileName);
                        throw new IOException(eMassage);
                    }
                    table.put(key, value);
                }
            }
        }
    }

    private Path getDirectoryPath(String key) {
        int hashCode = Math.abs(key.hashCode());
        int nDir = hashCode % 16;

        String directoryName = new StringBuilder().append(nDir).append(".dir").toString();
        return directoryPath.resolve(directoryName);
    }

    private Path getFilePath(String key) {
        int hashCode = Math.abs(key.hashCode());
        int nFile = hashCode / 16 % 16;

        String fileName = new StringBuilder().append(nFile).append(".dat").toString();
        return directoryPath.resolve(fileName);
    }
}
