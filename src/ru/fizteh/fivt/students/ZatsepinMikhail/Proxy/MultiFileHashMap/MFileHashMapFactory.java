package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class MFileHashMapFactory implements TableProviderFactory, AutoCloseable {
    private HashSet<MFileHashMap> pullOfMFileHashMap;
    private boolean isClosed;

    private void assertNotClosed() throws IllegalStateException {
        if (isClosed) {
            throw new IllegalStateException("table provider factory is closed");
        }
    }

    public MFileHashMapFactory() {
        pullOfMFileHashMap = new HashSet<>();
        isClosed = false;
    }

    @Override
    public void close() throws Exception {
        assertNotClosed();
       isClosed = true;
        for (MFileHashMap oneTableProvider : pullOfMFileHashMap) {
            oneTableProvider.close();
        }
    }

    @Override
    public TableProvider create(String dir) throws IOException {
        assertNotClosed();
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
        pullOfMFileHashMap.add(myMFileHashMap);
        return myMFileHashMap;
    }
}
