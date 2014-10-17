package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbfile;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBFile;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.util.ArrayList;


public class CommandPut extends Command {
    private DBTable dbTable;

    public void setDBTable(DBTable newDBTable) {
        dbTable = newDBTable;
    }

    @Override
    public void checkArgs(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing operand");
        }

        if (args.size() == 1) {
            throw new Exception("missing value");
        }
    }

    private void putValue(DBFile dbFile, String key, String value) {
        dbFile.getHashMap().put(key, value);
    }

    public void execute(ArrayList<String> args) throws Exception {
        checkArgs(args);

        String key = args.get(0);
        String value = args.get(1);

        int hashCode = key.hashCode();
        int nDirectory = hashCode % 16;
        int nFile = (hashCode / 16) % 16;

        DBFile dbFile = dbTable.getMapOfDBFiles().get(nDirectory).get(nFile);

        if (dbFile.getHashMap().containsKey(key)) {
            AbstractShell.printInformation("overwrite old value");
        } else {
            AbstractShell.printInformation("new");
        }

        putValue(dbFile, key, value);
    }
}
