package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import java.util.ArrayList;
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

        ArrayList<String> keysTable = new ArrayList<String>();
        for (Entry<String, String> entry : DbMain.fileStoredStringMap
                .entrySet()) {
            keysTable.add(entry.getKey());
        }

        System.out.println(String.join(", ", keysTable));

        return true;
    }

}
