package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;

public class Remove implements DBCommand {

    private String keyName;

    public Remove(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public boolean execute() {
        if (DbMain.fileStoredStringMap == null) {
            System.out.println("no table");
            return false;
        }

        String result = DbMain.fileStoredStringMap.remove(keyName);

        if (result != null) {
            System.out.println("removed");
            return true;
        } else {
            System.out.println("not found");
            return false;
        }
    }
}
