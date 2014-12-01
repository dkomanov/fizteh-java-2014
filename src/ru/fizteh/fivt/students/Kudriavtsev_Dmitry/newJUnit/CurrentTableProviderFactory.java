package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

/**
 * Created by Дмитрий on 02.11.2014.
 */
public class CurrentTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("dir is null or empty");
        }
        File f = new File(dir).getAbsoluteFile();
        if (!f.mkdirs()) {
            throw new IllegalArgumentException("Can't create directory: " + dir);
        }
        if (f.isFile()) {
            throw new IllegalArgumentException("db dir is a regular file");
        }
        if (!f.canWrite()) {
            throw new IllegalArgumentException("dir is read only");
        }
        return new CurrentTableProvider();
    }
}
