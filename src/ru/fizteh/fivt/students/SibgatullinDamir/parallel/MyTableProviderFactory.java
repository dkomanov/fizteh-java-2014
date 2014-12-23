package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.io.IOException;

/**
 * Created by Lenovo on 09.11.2014.
 */
public class MyTableProviderFactory implements TableProviderFactory{

    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException();
        }
        return new MyTableProvider(path);
    }
}
