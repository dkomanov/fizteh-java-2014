package ru.fizteh.fivt.students.Volodin_Denis.JUnit.database;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.TableProviderFactory;

public class TableProviderFactoryByVolodden implements TableProviderFactory {

    @Override
    public TableProviderByVolodden create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("wrong directory");
        }
        
        try {
            return new TableProviderByVolodden(dir);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
