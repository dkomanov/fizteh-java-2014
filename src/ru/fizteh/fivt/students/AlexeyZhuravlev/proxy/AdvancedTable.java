package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy;

import ru.fizteh.fivt.students.AlexeyZhuravlev.parallel.ParallelTable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.parallel.ParallelTableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTable;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author AlexeyZhuravlev
 */
public class AdvancedTable extends ParallelTable {
    public AdvancedTable(StructuredTable origin, ParallelTableProvider passedProvider,
                         ReentrantReadWriteLock passedLock) {
        super(origin, passedProvider, passedLock);
    }
}
