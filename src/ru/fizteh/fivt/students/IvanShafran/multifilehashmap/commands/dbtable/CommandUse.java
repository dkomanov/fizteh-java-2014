package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbtable;


import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.MultiFileHashMap;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CommandUse extends Command {
    private MultiFileHashMap multiFileHashMap;

    public void execute(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing name of table");
        }

        String tableName = args.get(0);

        File tableFile;
        try {
            tableFile =
                    new File(Paths.get(multiFileHashMap.getWorkingDirectory().getAbsolutePath(), tableName).toString());
        } catch (Exception e) {
            throw new Exception("wrong name of table");
        }

        if (!(new HashSet<>(Arrays.asList(multiFileHashMap.getWorkingDirectory().list()))).contains(tableName)) {
            multiFileHashMap.printInformation(tableName + " not exists");
            return;
        }

        DBTable dbTable = new DBTable(tableFile);

        if (multiFileHashMap.getWorkingDBTable() != null) {
            multiFileHashMap.getWorkingDBTable().writeToFile();
        }

        multiFileHashMap.setWorkingDBTable(dbTable);
        multiFileHashMap.printInformation("using " + tableName);
    }

    public CommandUse(MultiFileHashMap fileMap) {
        multiFileHashMap = fileMap;
    }

}
