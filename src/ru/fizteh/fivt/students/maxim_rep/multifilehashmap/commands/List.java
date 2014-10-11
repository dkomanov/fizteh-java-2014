package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.HexConverter;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.IoLib;

public class List implements DBCommand {

    String filePath;

    public List(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean execute() {
        if (DbMain.currentTable == null) {
            System.out.println("no table");
            return false;
        }

        String[] keys = IoLib.getTableKeyList(DbMain.currentTable, false);

        if (keys == null) {
            System.out.println("");
            return true;
        }

        for (String curLine : keys) {
            System.out.print(HexConverter.hexToString(curLine.substring(8))
                    + "; ");
        }
        System.out.println();
        return true;
    }

}
