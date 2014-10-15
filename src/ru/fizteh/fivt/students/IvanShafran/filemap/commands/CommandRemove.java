package ru.fizteh.fivt.students.IvanShafran.filemap.commands;

import ru.fizteh.fivt.students.IvanShafran.filemap.DBFile;
import ru.fizteh.fivt.students.IvanShafran.filemap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.filemap.abstractShell.Command;

import java.util.ArrayList;


public class CommandRemove extends Command {
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
            AbstractShell.printInformation("remove");
        } else {
            AbstractShell.printInformation("not found");
        }

        dataBaseFile.getHashMap().remove(key);
        dataBaseFile.writeHashMapToFile();
    }

    public CommandRemove(DBFile file) {
        dataBaseFile = file;
    }
}
