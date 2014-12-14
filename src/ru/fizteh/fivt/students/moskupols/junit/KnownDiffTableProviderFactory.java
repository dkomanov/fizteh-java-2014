package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

/**
 * Created by moskupols on 14.12.14.
 */
public interface KnownDiffTableProviderFactory extends TableProviderFactory {
    @Override
    KnownDiffTableProvider create(String dir);
}
