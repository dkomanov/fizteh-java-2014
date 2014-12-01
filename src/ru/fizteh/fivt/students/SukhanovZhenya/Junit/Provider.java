package ru.fizteh.fivt.students.SukhanovZhenya.Junit;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;

public class Provider implements TableProvider {
    private static File currentDir;

    public Provider(String dir) {
        currentDir = new File(dir);
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null argument");
        }

        File tmp = new File(currentDir.getAbsolutePath() + "/" + name);
        if (!tmp.exists()) {
            return null;
        }
        return new FileMap(currentDir + "/" + name);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null argument");
        }

        File tmp = new File(currentDir.getAbsolutePath() + "/" + name);
        if (tmp.exists()) {
            return null;
        }

        if (!tmp.mkdir()) {
            System.err.println("Can not create Table");
            System.exit(1);
        }

        return new FileMap(currentDir.getAbsolutePath() + "/" + name);
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null argument");
        }

        File table = new File(currentDir.getAbsolutePath() + "/" + name);
        if (!table.exists()) {
            throw new IllegalStateException("Doesn't exist");
        }

        FileMap tmp = (FileMap) getTable(name);
        tmp.remove();
    }


}
