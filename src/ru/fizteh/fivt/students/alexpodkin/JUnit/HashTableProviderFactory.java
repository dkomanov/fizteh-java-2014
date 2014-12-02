package ru.fizteh.fivt.students.alexpodkin.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * Created by Alex on 11.11.14.
 */
public class HashTableProviderFactory implements TableProviderFactory {

    public HashTableProviderFactory() {}

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Table name shouldn't be null");
        }
        File db = new File(dir);
        if (db.exists() && !db.isDirectory()) {
            throw new IllegalArgumentException(dir + "isn't a directory");
        }
        if (!db.exists()) {
            if (!db.mkdir()) {
                throw new IllegalArgumentException("Can't create this directory");
            }
        }
        return new HashTableProvider(dir);
    }
}
