package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands.CreateCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands.DropCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands.MultiFileHashMapExitCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands.ShowCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands.UseCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd.Command;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd.GetCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd.ListCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd.PutCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd.RemoveCommand;

public final class MultiFileHashMapManager {
    private Map<String, Command> commands;
    private MultiFileTable workTable;
    private HashMap<String, MultiFileTable> tables;

    public MultiFileHashMapManager() throws IOException {
        tables = new HashMap<>();
        File dir = new File(System.getProperty("fizteh.db.dir"));
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir.getName() + " "
                    + "is not directory");
        }
        if (!dir.exists()) {
            throw new FileNotFoundException(dir.getName() + " " + "not exists");
        }
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                throw new NotDirectoryException("Table should be directory");
            }
            MultiFileTable table = new MultiFileTable(file.getAbsolutePath());
            tables.put(file.getName(), table);
        }
        workTable = null;
        commands = new HashMap<>();
        commands.put("create", new CreateCommand());
        commands.put("drop", new DropCommand());
        commands.put("use", new UseCommand());
        commands.put("show", new ShowCommand());
        commands.put("list", new ListCommand());
        commands.put("put", new PutCommand());
        commands.put("get", new GetCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("exit", new MultiFileHashMapExitCommand());
    }

    public void parse(final String cmdArgs, final Boolean cmdMode)
            throws Exception {
        try {
            String[] parseArgs = cmdArgs.split(" ");
            if (commands.get(parseArgs[0]) != null) {
                commands.get(parseArgs[0]).execute(parseArgs, this);
            } else {
                throw new IllegalArgumentException("Wrong arguments");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (cmdMode) {
                System.exit(1);
            }
        }
    }

    public void setWorkTable(final String value) {
        workTable = tables.get(value);
    }

    public MultiFileTable getWorkTable() {
        return workTable;
    }

    public void putTables(final String key, final MultiFileTable value) {
        tables.put(key, value);
    }

    public HashMap<String, MultiFileTable> getTables() {
        return tables;
    }
}
