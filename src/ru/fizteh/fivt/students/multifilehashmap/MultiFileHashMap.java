package ru.fizteh.fivt.students.multifilehashmap;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by Lenovo on 20.10.2014.
 */
public class MultiFileHashMap extends HashMap<String, FileMap> {

    Path location;

    MultiFileHashMap(Path path) throws MyException {
        location = path;

        File currentDir = new File(path.toString());
        File[] files = currentDir.listFiles();
        clear();
        for (File file: files) {
            if (!file.isDirectory()) {
                throw new MyException(file.toString() + " is not a directory");
            }
            FileMap filemap = new FileMap(file.toPath());
            put(file.getName(), filemap);
        }
    }
}
