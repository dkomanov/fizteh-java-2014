package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBFile;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.util.ArrayList;


public class CommandGet extends Command {
    private DBFile dataBaseFile;

    @Override
    public void checkArgs(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing key");
        }
    }

    public void execute(ArrayList<String> args) throws Exception {
        checkArgs(args);

        String key = args.get(0);

        if (dataBaseFile.getHashMap().containsKey(key)) {
            AbstractShell.printInformation("found");
        } else {
            AbstractShell.printInformation("not found");
        }

        AbstractShell.printInformation(dataBaseFile.getHashMap().get(key));
    }

    public CommandGet(DBFile file) {
        dataBaseFile = file;
    }
}
