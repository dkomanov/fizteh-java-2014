package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class MyTableProvider implements TableProvider {

    private JUnitDataBaseDir directory;

    public MyTableProvider(String path) {
        try {
            directory = new JUnitDataBaseDir(path);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (directory.tables.containsKey(name)) {
            return new MyTable(directory.tables.get(name), name);
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (directory.tables.containsKey(name)) {
            return null;
        } else {
            JUnitCommand create = new JUnitCreateCommand(name);
            PrintStream out = System.out;
            System.setOut(new PrintStream(new DummyOutputStream()));
            try {
                create.execute(directory);
            } catch (Exception e) {
                throw new IllegalArgumentException();
            }
            System.setOut(out);
            return getTable(name);
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!directory.tables.containsKey(name)) {
            throw new IllegalStateException();
        }
        JUnitCommand drop = new JUnitDropCommand(name);
        PrintStream out = System.out;
        System.setOut(new PrintStream(new DummyOutputStream()));
        try {
            drop.execute(directory);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        System.setOut(out);
    }

    public JUnitDataBaseDir getJUnitDir() {
        return directory;
    }
}
