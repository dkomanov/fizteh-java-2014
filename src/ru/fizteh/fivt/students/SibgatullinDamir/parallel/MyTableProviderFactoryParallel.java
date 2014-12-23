package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.io.IOException;

/**
 * Created by Lenovo on 02.12.2014.
 */
public class MyTableProviderFactoryParallel implements TableProviderFactory {
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException();
        } else {
            return new MyTableProviderParallel(path);
        }
    }
}
