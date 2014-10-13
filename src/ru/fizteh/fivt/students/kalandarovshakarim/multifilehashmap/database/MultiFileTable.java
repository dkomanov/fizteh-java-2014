/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
public final class MultiFileTable extends AbstractTable {

    private Path directoryPath;

    public MultiFileTable(String directory, String tableName)
            throws FileNotFoundException, IOException {
        super(tableName);
        directoryPath = Paths.get(directory, tableName);
        try {
            Files.createDirectory(directoryPath);
        } catch (NoSuchFileException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (FileAlreadyExistsException e) {
            load();
        }
    }

    @Override
    protected void load() throws IOException {
        for (String dirName : directoryPath.toFile().list()) {
            File dirFile = directoryPath.resolve(dirName).toFile();
            if (!dirFile.isDirectory()) {
                String format = "%s: Table directory contains illegal files";
                throw new IOException(String.format(format, getName()));
            }
            readDir(dirFile);
        }
    }

    @Override
    protected void save() throws IOException {
        ShellUtils utils = new ShellUtils();
        utils.rm(directoryPath.toString(), true);
        Files.createDirectory(directoryPath);

        TableWriter[][] writers = new TableWriter[16][16];

        for (Entry<String, String> entry : table.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int hashCode = Math.abs(key.hashCode());
            int nDir = hashCode % 16;
            int nFile = hashCode / 16 % 16;

            Path dirPath = directoryPath.resolve(getDirName(nDir));
            Path filePath = dirPath.resolve(getFileName(nFile));

            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }

            if (writers[nDir][nFile] == null) {
                writers[nDir][nFile] = new TableWriter(filePath.toString());
            }

            writers[nDir][nFile].write(key);
            writers[nDir][nFile].write(value);
        }

        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                if (writers[dir][file] != null) {
                    writers[dir][file].close();
                }
            }
        }
    }

    private void readDir(File directory) throws IOException {
        for (String file : directory.list()) {
            String fileName = new File(directory, file).toString();
            try (TableReader reader = new TableReader(fileName)) {
                String key;
                String value;

                while (!reader.eof()) {
                    key = reader.read();
                    value = reader.read();
                    table.put(key, value);
                }
            }
        }
    }

    private String getDirName(int nDir) {
        return new StringBuilder().append(nDir).append(".dir").toString();
    }

    private String getFileName(int nFile) {
        return new StringBuilder().append(nFile).append(".dat").toString();
    }
}
