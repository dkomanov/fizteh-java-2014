package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.MultiFileHashMap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MFileHashMapFactory implements TableProviderFactory {
    public MFileHashMapFactory() {}

    public TableProvider create(String dir) throws IOException {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        Path dataBaseDirectory
                = Paths.get(System.getProperty("user.dir")).resolve(dir);

        if (Files.exists(dataBaseDirectory)) {
            if (!Files.isDirectory(dataBaseDirectory)) {
                throw new IllegalArgumentException();
            }
        } else {
            Files.createDirectory(dataBaseDirectory);
        }

        MFileHashMap myMFileHashMap = new MFileHashMap(dataBaseDirectory.toString());
        if (!myMFileHashMap.init()) {
            throw new IOException("error while initialization");
        }
        return myMFileHashMap;
    }
}
