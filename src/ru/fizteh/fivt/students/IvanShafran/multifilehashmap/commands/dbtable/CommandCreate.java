package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbtable;


import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.MultiFileHashMap;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CommandCreate extends Command {
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

        if (tableFile.exists()) {
            AbstractShell.printInformation(tableName + " exists");
            return;
        }

        try {
            tableFile.mkdir();
        } catch (Exception e) {
            throw new Exception(tableName + " didn't create");
        }

        AbstractShell.printInformation("created");
    }

    public CommandCreate(MultiFileHashMap fileMap) {
        multiFileHashMap = fileMap;
    }

}
