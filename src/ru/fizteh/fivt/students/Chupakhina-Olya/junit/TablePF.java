package ru.fizteh.fivt.students.olga_chupakhina.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TablePF implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new PvTable(dir);
    }
}