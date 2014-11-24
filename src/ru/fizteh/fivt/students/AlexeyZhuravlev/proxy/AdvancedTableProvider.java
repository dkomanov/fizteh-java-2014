package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy;

import ru.fizteh.fivt.students.AlexeyZhuravlev.parallel.ParallelTableProvider;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class AdvancedTableProvider extends ParallelTableProvider {
    protected AdvancedTableProvider(String path) throws IOException {
        super(path);
    }
}
