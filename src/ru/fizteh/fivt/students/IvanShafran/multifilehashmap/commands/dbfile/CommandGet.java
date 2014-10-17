package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbfile;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBFile;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.util.ArrayList;


public class CommandGet extends Command {
    private DBTable dbTable;

    @Override
    public void checkArgs(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing key");
        }
    }

    public void setDBTable(DBTable newDBTable) {
        dbTable = newDBTable;
    }

    public void execute(ArrayList<String> args) throws Exception {
        checkArgs(args);

        if (dbTable == null) {
            throw new Exception("not selected any table");
        }

        String key = args.get(0);

        int hashCode = key.hashCode();
        int nDirectory = hashCode % 16;
        int nFile = (hashCode / 16) % 16;

        DBFile dbFile = dbTable.getMapOfDBFiles().get(nDirectory).get(nFile);

        if (dbFile.getHashMap().containsKey(key)) {
            AbstractShell.printInformation("found");
            AbstractShell.printInformation(dbFile.getHashMap().get(key));
        } else {
            AbstractShell.printInformation("not found");
        }
    }

}
