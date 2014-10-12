/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

import java.io.FileNotFoundException;
import java.io.IOException;
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

    private ShellUtils utils = new ShellUtils();

    public MultiFileTable(String directory, String tableName)
            throws FileNotFoundException, IOException {
        super(tableName);
        utils.chDir(directory);
        try {
            utils.mkDir(tableName);
        } catch (IOException e) {
            load();
        }
    }

    @Override
    protected void load() throws IOException {
        utils.chDir(getName());
        for (String dir : utils.listFiles()) {
            try {
                utils.chDir(dir);
                readDir();
                utils.chDir("..");
            } catch (IOException e) {
                // Dir is not Directory.
            }
        }
        utils.chDir("..");
    }

    @Override
    protected void save() throws IOException {
        utils.rm(getName(), true);
        utils.mkDir(getName());
        utils.chDir(getName());

        for (Entry<String, String> entry : table.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int hashCode = Math.abs(key.hashCode());

            try {
                utils.mkDir(getDirName(hashCode));
            } catch (IOException e) {
                // Directory already exists.
            }

            utils.chDir(getDirName(hashCode));

            String filePath = utils.getPath(getFileName(hashCode)).toString();

            try (TableWriter writer = new TableWriter(filePath)) {
                writer.write(key);
                writer.write(value);
            }

            utils.chDir("..");
        }
        utils.chDir("..");
    }

    private void readDir() throws IOException {
        for (int file = 0; file < 16; ++file) {
            String fileName = getFileName(file * 16);
            String pathToFile = utils.getPath(fileName).toString();
            try (TableReader reader = new TableReader(pathToFile)) {
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

    private String getDirName(int hashCode) {
        int nDir = hashCode % 16;
        return new StringBuilder().append(nDir).append(".dir").toString();
    }

    private String getFileName(int hashCode) {
        int nFile = hashCode / 16 % 16;
        return new StringBuilder().append(nFile).append(".dat").toString();
    }
}
