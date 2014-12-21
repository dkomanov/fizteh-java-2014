package ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata;

import ru.fizteh.fivt.students.VasilevKirill.parallel.structures.MultiMap;
import ru.fizteh.fivt.students.VasilevKirill.parallel.structures.MultiTable;
import ru.fizteh.fivt.students.VasilevKirill.parallel.structures.FileMap;

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
