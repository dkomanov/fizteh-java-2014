package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DatabaseException;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;

public class Exit implements DBCommand {

    @Override
    public boolean execute() {
        if (DbMain.fileStoredStringMap != null) {
            try {
                if (DbMain.fileStoredStringMap != null) {
                    DbMain.fileStoredStringMap.close();
                }
            } catch (Exception e) {
                System.err.println((new DatabaseException(e.toString())
                        .toString()));
                return false;
            }
        }
        return true;
    }

}
