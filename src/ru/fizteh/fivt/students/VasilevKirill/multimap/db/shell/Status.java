package ru.fizteh.fivt.students.VasilevKirill.multimap.db.shell;

import ru.fizteh.fivt.students.VasilevKirill.multimap.MultiMap;
import ru.fizteh.fivt.students.VasilevKirill.multimap.db.FileMap;

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
}
