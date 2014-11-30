package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbtable;


import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.MultiFileHashMap;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.shell.CommandRM;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CommandDrop extends Command {
    private MultiFileHashMap multiFileHashMap;

    public void execute(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing name of table");
        }

        String tableName = args.get(0);

        if (Paths.get(tableName).toString().contains(File.separator)) {
            throw new Exception(tableName + " contains separator: / or \\");
        }

        File tableFile;
        try {
            tableFile =
                    new File(multiFileHashMap.getWorkingDirectory().getAbsolutePath() + File.separator + tableName);
        } catch (Exception e) {
            throw new Exception("wrong name of table");
        }

        if (!tableFile.exists()) {
            AbstractShell.printInformation(tableName + " not exists");
            return;
        }

        try {
            CommandRM commandRM = new CommandRM();
            commandRM.execute(tableFile.getAbsoluteFile().toString());
            if (multiFileHashMap.getWorkingDBTable() != null && multiFileHashMap.getWorkingDBTable().
                    getWorkingDirectory().getAbsolutePath().equals(tableFile.getAbsolutePath())) {
                multiFileHashMap.setWorkingDBTable(null);
            }
        } catch (Exception e) {
            throw new Exception(tableName + " didn't drop: " + e.getMessage());
        }

        AbstractShell.printInformation("dropped");
    }

    public CommandDrop(MultiFileHashMap fileMap) {
        multiFileHashMap = fileMap;
    }

}
