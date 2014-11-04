package ru.fizteh.fivt.students.alina_chupakhina.junit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

/**
 * Created by opa on 04.11.2014.
 */
public class TablePF implements TableProviderFactory {

    @Override
    public PvTable create(String dir) {
        return new PvTable(dir);
    }
}
