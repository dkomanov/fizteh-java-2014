package ru.fizteh.fivt.students.pershik.JUnit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * Created by pershik on 10/28/14.
 */
public class HashTableProviderFactory implements TableProviderFactory {

    public HashTableProviderFactory() {
    }

    @Override
    public HashTableProvider create(String dir) throws IllegalArgumentException {
        checkName(dir);
        File dbDir = new File(dir);
        if (dbDir.exists() && !dbDir.isDirectory()) {
            throw new IllegalArgumentException(dir + " isn't directory");
        }
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                throw new IllegalArgumentException("Can't create " + dir);
            }
        }
        return new HashTableProvider(dir);
    }

    private void checkName(String name) throws IllegalArgumentException {
        if (name == null || "..".equals(name) || ".".equals(name)
                || name.contains(File.separator)) {
            throw new IllegalArgumentException("Incorrect name");
        }
    }
}
