package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;

public class Get implements DBCommand {

    private String keyName;

    public Get(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public boolean execute() {

        if (DbMain.fileStoredStringMap == null) {
            System.out.println("no table");
            return false;
        }

        String result = DbMain.fileStoredStringMap.get(keyName);
        if (result == null) {
            System.out.println("not found");
            return false;
        } else {
            System.out.println("found");
            System.out.println(result);
        }
        return true;
    }
}
