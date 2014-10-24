package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.*;

public class Put implements DBCommand {
    String keyName;
    String dataText;

    public Put(String keyName, String dataText) {
        this.keyName = keyName;
        this.dataText = dataText;
    }

    @Override
    public boolean execute() {
        if (DbMain.fileStoredStringMap == null) {
            System.out.println("no table");
            return false;
        }
        if (DbMain.fileStoredStringMap.containsKey(keyName)) {
            System.out.println("overwrite");
            System.out.println(DbMain.fileStoredStringMap.replace(keyName,
                    dataText));
        } else {
            DbMain.fileStoredStringMap.put(keyName, dataText);
            System.out.println("new");
        }
        return true;

    }
}
