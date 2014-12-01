package ru.fizteh.fivt.students.standy66_new.storage.strings;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * Created by astepanov on 20.10.14.
 */
public class StringDatabaseFactory implements TableProviderFactory {
    @Override
    public StringDatabase create(String dir) {
        if ((dir == null) || dir.isEmpty()) {
            throw new IllegalArgumentException("dir is null or empty");
        }
        File file = new File(dir).getAbsoluteFile();
        return new StringDatabase(file);
    }

    public StringDatabase create(File file) {
        return create(file.getAbsolutePath());
    }
}
