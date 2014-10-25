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

        StringBuilder list = new StringBuilder("");
        for (Entry<String, String> entry : DbMain.fileStoredStringMap
                .entrySet()) {
            list.append(entry.getKey() + ", ");
        }

        if (!list.toString().equals("")
                && list.charAt(list.length() - 1) == ' ' && list.charAt(list.length() - 2) == ',') {
            list.deleteCharAt(list.length() - 1);
            list.deleteCharAt(list.length() - 1);
        }

        System.out.println(list.toString());
        return true;
    }

}
