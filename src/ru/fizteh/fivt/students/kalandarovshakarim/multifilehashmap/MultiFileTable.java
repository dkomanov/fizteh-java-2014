/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap;

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

    protected ShellUtils tableUtil = new ShellUtils();

    public MultiFileTable(String directory, String tableName) throws IOException {
        super(tableName);
        chDirTry(directory);
        if (!mkDirTry(tableName)) {
            load();
        }
    }

    @Override
    protected void load() throws IOException {
        if (chDirTry(getName())) {
            for (int dir = 0; dir < 16; ++dir) {
                if (chDirTry(getDirName(dir))) {
                    readDir();
                    chDirTry("..");
                }
            }
            chDirTry("..");
        }
    }

    @Override
    protected void save() throws IOException {
        rmTry(getName());
        mkDirTry(getName());
        chDirTry(getName());

        for (Entry<String, String> entry : table.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            int hashCode = Math.abs(key.hashCode());

            mkDirTry(getDirName(hashCode));
            chDirTry(getDirName(hashCode));

            String fileName = tableUtil.getPath(getFileName(hashCode)).toString();

            try (TableWriter writer = new TableWriter(fileName)) {
                writer.write(key);
                writer.write(value);
            }
            chDirTry("..");
        }
        chDirTry("..");
    }

    private void readDir() throws IOException {
        for (int file = 0; file < 16; ++file) {
            String fileName = getFileName(file * 16);
            String pathToFile = tableUtil.getPath(fileName).toString();
            try (TableReader reader = new TableReader(pathToFile)) {
                String key;
                String value;

                while (!reader.eof()) {
                    key = reader.read();
                    value = reader.read();
                    table.put(key, value);
                    //System.out.println(key + " " + value);
                }
            }
        }
    }

    private boolean chDirTry(String pathName) {
        try {
            tableUtil.chDir(pathName);
            System.out.println(tableUtil.getCwd());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean mkDirTry(String dirName) {
        try {
            tableUtil.mkDir(dirName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean rmTry(String dirName) {
        try {
            tableUtil.rm(dirName, true);
            return true;
        } catch (Exception e) {
            return false;
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
