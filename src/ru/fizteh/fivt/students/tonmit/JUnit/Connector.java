package ru.fizteh.fivt.students.tonmit.JUnit;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Connector {
    protected Map<String, JUnitCommand> commands = new HashMap<>();
    protected Path dbRoot;
    protected Map<String, CurrentTable> tables;
    protected CurrentTable activeTable;
    protected CurrentTableProvider activeTableProvider = new CurrentTableProvider();

    public Connector(Path dbPath) {
        if (!Files.exists(dbPath)) {
            System.err.println("destination does not exist");
            System.exit(-1);
        }
        if (!Files.isDirectory(dbPath)) {
            System.err.println("destination is not a directory");
            System.exit(-1);
        }
        dbRoot = dbPath;
        open();

        JUnitCommand[] commandsArray = {new Create(), new Drop(), new Use(), new Show(),
                                    new Put(), new Get(), new List(), new Remove(),
                                    new Commit(), new Rollback(), new Size()};
        for (JUnitCommand command : commandsArray) {
            commands.put(command.name, command);
        }
    }

    private void open() {
        if (tables == null) {
            tables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRoot)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        CurrentTable table;
                        try {
                        table = new CurrentTable(file);
                        } catch (IOException e) {
                            System.err.println("can't create directory: " + file.toString());
                            table = null;
                            System.exit(-1);
                        }
                        table.load(file.getFileName().toString());
                        tables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                System.err.println("can't load the database");
                System.exit(-1);
            }

        }
    }

    public void close() {
        if (tables != null) {
            for (CurrentTable table : tables.values()) {
                table.unload(table, table.getName());
            }
        }
        if (activeTable != null) {
            activeTable.unload(activeTable, activeTable.getName());
        }
    }

    public boolean run(String name, String[] args, boolean batchMode, boolean batchModeInInteractive) {
        JUnitCommand command = commands.get(name);
        command.batchMode = batchMode;
        command.batchModeInInteractive = batchModeInInteractive;
        if (command != null) {
            if (!command.exec(this, args)) {
                return false;
            }
        } else if (!args[0].equals("")) {
            System.err.println("args[0] + : command not found");
            return false;
        }
        return true;
    }
}
