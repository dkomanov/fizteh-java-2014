package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import java.util.Map.Entry;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;

public class List implements DBCommand {

    public List() {
    }

    @Override
    public boolean execute() {
        if (DbMain.fileStoredStringMap == null) {
            System.out.println("no table");
            return false;
        }

        for (Entry<String, String> entry : DbMain.fileStoredStringMap
                .entrySet()) {
            System.out.print(entry.getKey() + ";");
        }
        System.out.println();
        return true;
    }

}
