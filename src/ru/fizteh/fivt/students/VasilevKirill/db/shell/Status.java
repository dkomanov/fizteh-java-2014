package ru.fizteh.fivt.students.VasilevKirill.db.shell;

import ru.fizteh.fivt.students.VasilevKirill.db.FileMap;

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
}
