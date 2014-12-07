package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {
    @Override
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
