package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMap;

/**
 * Created by moskupols on 14.12.14.
 */
public interface MultiFileMapTableAdaptorFactory {
    KnownDiffTable adapt(MultiFileMap multiFileMap);
}
