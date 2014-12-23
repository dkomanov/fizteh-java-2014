package ru.fizteh.fivt.students.Bulat_Galiev.multifilehashmap;

public class TableProviderFactory {
    final TableProvider create(final String dir) {
        if (dir != null) {
            return new TableProvider(dir);
        } else {
            throw new IllegalArgumentException("Null name directory.");
        }
    }
}
