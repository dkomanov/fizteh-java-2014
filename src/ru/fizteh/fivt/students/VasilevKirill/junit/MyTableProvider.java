package ru.fizteh.fivt.students.VasilevKirill.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.MultiMap;

import java.io.IOException;

/**
 * Created by Kirill on 10.11.2014.
 */
public class MyTableProvider implements TableProvider {
    private MultiMap tables;

    public MyTableProvider(String workingDirectory) {
        try {
            tables = new MultiMap(workingDirectory);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            if (!tables.addTable(name)) {
                return null;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public Table getTable(String name) {
        return null;
    }

    @Override
    public void removeTable(String name) {

    }
}
