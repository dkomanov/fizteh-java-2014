package ru.fizteh.fivt.students.standy66_new.storage.strings;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * Created by astepanov on 20.10.14.
 */
public class StringDatabaseFactory implements TableProviderFactory, AutoCloseable {
    private boolean closed = false;

    @Override
    public StringDatabase create(String dir) {
        assertNotClosed();
        if ((dir == null) || dir.isEmpty()) {
            throw new IllegalArgumentException("dir is null or empty");
        }
        File file = new File(dir).getAbsoluteFile();
        return new StringDatabase(file);
    }

    public StringDatabase create(File file) {
        assertNotClosed();
        return create(file.getAbsolutePath());
    }

    @Override
    public void close() throws Exception {
        if (!closed) {
            closed = true;
        }
    }

    private void assertNotClosed() {
        if (closed) {
            throw new IllegalStateException("StringDatabaseFactory had been closed, but then method called");
        }
    }
}
