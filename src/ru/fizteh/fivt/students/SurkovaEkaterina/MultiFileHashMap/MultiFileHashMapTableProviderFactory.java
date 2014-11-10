package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * Created by kate on 09.11.14.
 */
public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {
    public MultiFileHashMapTableProvider create(String directory) {
        if (directory == null || directory.equals("")) {
            throw new IllegalArgumentException("directory name cannot be empty");
        }
        File databaseDirectory = new File(directory);
        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("set directory, not file");
        }
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir();
        }
        return new MultiFileHashMapTableProvider(databaseDirectory.getAbsolutePath());
    }
}
