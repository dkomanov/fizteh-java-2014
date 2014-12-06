package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata;

import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.FileMap;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTable;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

/**
 * Created by Kirill on 19.10.2014.
 */
public class Status {
    Object object;

    public Status(Object object) {
        this.object = object;
    }

    public FileMap getFileMap() {
        return object instanceof FileMap ? (FileMap) object : null;
    }

    public MyTableProvider getMultiMap() {
        return object instanceof MyTableProvider ? (MyTableProvider) object : null;
    }

    public MyTable getMultiTable() {
        return object instanceof MyTable ? (MyTable) object : null;
    }
}
