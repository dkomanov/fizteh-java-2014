package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Дмитрий on 09.10.14.
 */
public class Welcome {
    private final Path dbRoot;
    private Map<String, StoreableTable> tables;
    private StoreableTable activeTable;
    private final StoreableTableProvider activeTableProvider;

    public Map<String, Command> commands = new HashMap<>();

    public Map<String, StoreableTable> getTables() {
        return tables;
    }

    public StoreableTable getActiveTable() {
        return activeTable;
    }

    public void setActiveTable(StoreableTable activeTable) {
        this.activeTable = activeTable;
    }

    public StoreableTableProvider getActiveTableProvider() {
        return activeTableProvider;
    }

    public Welcome(Path dbPath, String[] args) throws IOException, IllegalStateException {
        if (!Files.exists(dbPath)) {
            System.err.println("destination does not exist");
            System.exit(-1);
        }
        if (!Files.isDirectory(dbPath)) {
            System.err.println("destination is not a directory");
            System.exit(-1);
        }
        activeTableProvider = new StoreableTableProvider(dbPath.toString());
        dbRoot = dbPath;
        open();

        commands = allCommands();

        Interpreter interpreter = new Interpreter(this, System.in, System.out, System.err);
        if (args != null && args.length != 0) {
            interpreter.batchMode(args);
        } else {
            interpreter.interactiveMode();
        }

        close();
    }

    public Welcome() {
        dbRoot = null;
        activeTableProvider = null;
        commands = allCommands();
    }

    public Map<String, Command> allCommands() {
        Map<String, Command> result = new HashMap<>();
        Command[] commandsArray = {new Create(), new Drop(), new Use(), new Show(),
                new Put(), new Get(), new List(), new Remove(),
                new Commit(), new Rollback(), new Size(), new Test()};
        for (Command command : commandsArray) {
            result.put(command.name, command);
        }
        return result;
    }

    private void open() {
        if (tables == null) {
            tables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRoot)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        StoreableTable table;
                        try {
                            table = new StoreableTable(file);
                        } catch (IOException e) {
                            System.err.println("can't create directory: " + file.toString());
                            table = null;
                            System.exit(-1);
                        }
                        table.readDb();
                        tables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                System.err.println("can't load the database");
                System.exit(-1);
            }
        }
    }

    protected void close() {
        if (tables != null) {
            for (StoreableTable table : tables.values()) {
                table.unload(table, table.getName());
            }
        }
        if (activeTable != null) {
            activeTable.unload(activeTable, activeTable.getName());
        }
    }

}
