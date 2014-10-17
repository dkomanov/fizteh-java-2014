package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbfile;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBFile;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.util.ArrayList;


public class CommandList extends Command {
    private DBFile dataBaseFile;

    public void execute(ArrayList<String> args) throws Exception {
        StringBuilder list = new StringBuilder();
        int index = 0;
        for (String key : dataBaseFile.getHashMap().keySet()) {
            list.append(key);
            ++index;

            if (index != dataBaseFile.getHashMap().size()) {
                list.append(", ");
            }
        }

        AbstractShell.printInformation(list.toString());
    }

    public CommandList(DBFile file) {
        dataBaseFile = file;
    }
}
