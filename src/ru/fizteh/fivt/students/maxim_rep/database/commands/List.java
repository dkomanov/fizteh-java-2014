package ru.fizteh.fivt.students.maxim_rep.database.commands;

import ru.fizteh.fivt.students.maxim_rep.database.HexConverter;
import ru.fizteh.fivt.students.maxim_rep.database.IoLib;

public class List implements DBCommand {

    String filePath;

    public List(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean execute() {
        String[] keys = IoLib.getKeyList(filePath, false, false);

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
