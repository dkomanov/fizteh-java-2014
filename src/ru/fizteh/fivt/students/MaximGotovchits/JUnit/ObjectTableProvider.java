package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;

public class ObjectTableProvider extends CommandTools implements TableProvider {
    public ObjectTableProvider() {
        dataBaseName = System.getProperty("fizteh.db.dir");
    }
    public ObjectTableProvider(String dir) {
        dataBaseName = dir;
    }
    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        checkException(name);
        if (new File(dataBaseName + "/" + name).exists()) {
            return new ObjectTable(name);
        }
        return null;
    }
    @Override
    public Table createTable(String name) throws IllegalArgumentException {
        checkException(name);
        dataBaseName = System.getProperty("fizteh.db.dir");
        File file = new File(dataBaseName + "/" + name);
        if (file.exists()) {
            System.out.println(name + " exists");
        } else {
            file.mkdir();
            System.out.println("created");
        }
        return new ObjectTable(name);
    }
    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        dataBaseName = System.getProperty("fizteh.db.dir");
        try {
            if (name == null || name.length() > longestName) {
                throw new IllegalArgumentException();
            }
            String tableName = dataBaseName + "/" + name;
            if (new File(tableName).exists()) {
                recRem(dataBaseName + "/" + name);
                System.out.println("dropped");
            } else {
                throw new IllegalStateException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return;
        } catch (IllegalStateException s) {
            System.err.println(s);
        }
    }
    void recRem(String myFile) {
        File file = new File(myFile);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recRem(f.getAbsolutePath());
            }
        }
        file.delete();
    }
    public Object checkException(String name) throws IllegalArgumentException {
        try {
            if (name == null || name.length() > longestName) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return null;
        }
        return null;
    }
}
