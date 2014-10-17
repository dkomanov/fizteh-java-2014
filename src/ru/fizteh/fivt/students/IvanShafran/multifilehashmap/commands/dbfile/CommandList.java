package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbfile;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBFile;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.util.ArrayList;


public class CommandList extends Command {
    private DBTable dbTable;

    public void setDBTable(DBTable newDBTable) {
        dbTable = newDBTable;
    }

    private String getList(DBFile dbFile) {
        StringBuilder list = new StringBuilder();
        int index = 0;
        for (String key : dbFile.getHashMap().keySet()) {
            list.append(key);
            ++index;

            if (index != dbFile.getHashMap().size()) {
                list.append(", ");
            }
        }

        return list.toString();
    }

    public void execute(ArrayList<String> args) throws Exception {
        if (dbTable == null) {
            throw new Exception("not selected any table");
        }

        StringBuilder list = new StringBuilder();
        int index = 0;

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                String dbFileList = getList(dbTable.getMapOfDBFiles().get(i).get(j));

                if (index != 0) {
                    list.append(", ");
                }

                if (dbFileList.length() != 0) {
                    ++index;
                    list.append(dbFileList);
                }
            }
        }

        AbstractShell.printInformation(list.toString());
    }

}
