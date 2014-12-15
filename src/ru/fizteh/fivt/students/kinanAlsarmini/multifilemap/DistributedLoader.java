package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap;

import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.FilemapReader;
import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.TableBuilder;

import java.io.File;
import java.io.IOException;

public class DistributedLoader {
    public static void load(TableBuilder builder) throws IOException {
        File tableDirectory = builder.getTableDirectory();

        if (tableDirectory.listFiles() == null) {
            return;
        }

        for (final File bucket : tableDirectory.listFiles()) {
            if (bucket.isFile()) {
                continue;
            }

            if (bucket.listFiles().length == 0) {
                throw new IllegalArgumentException("empty bucket");
            }

            for (final File file : bucket.listFiles()) {
                builder.setCurrentFile(file);
                FilemapReader.loadFromFile(file.getAbsolutePath(), builder);
            }
        }
    }
}
