package ru.fizteh.fivt.students.SibgatullinDamir.junit;

/**
 * Created by Lenovo on 09.11.2014.
 */
public class MyTableProviderFactory implements TableProviderFactory{

    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        return new MyTableProvider(dir);
    }
}
