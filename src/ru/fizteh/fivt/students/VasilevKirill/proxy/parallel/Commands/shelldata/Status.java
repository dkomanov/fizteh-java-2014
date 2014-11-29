package ru.fizteh.fivt.students.VasilevKirill.proxy.parallel.Commands.shelldata;

import ru.fizteh.fivt.students.VasilevKirill.proxy.parallel.structures.FileMap;
import ru.fizteh.fivt.students.VasilevKirill.proxy.parallel.structures.MultiMap;
import ru.fizteh.fivt.students.VasilevKirill.proxy.parallel.structures.MultiTable;

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

    public MultiMap getMultiMap() {
        return object instanceof MultiMap ? (MultiMap) object : null;
    }

    public MultiTable getMultiTable() {
        return object instanceof MultiTable ? (MultiTable) object : null;
    }
}
