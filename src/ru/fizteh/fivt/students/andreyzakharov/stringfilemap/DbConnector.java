package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DbConnector implements AutoCloseable {
    Map<String, Command> commands = new HashMap<>();
    Path dbRoot;
    Map<String, FileMap> tables;
    FileMap activeTable;

    DbConnector(Path dbPath) throws ConnectionInterruptException {
        if (!Files.exists(dbPath)) {
            throw new ConnectionInterruptException("connection: destination does not exist");
        }
        if (!Files.isDirectory(dbPath)) {
            throw new ConnectionInterruptException("connection: destination is not a directory");
        }
        dbRoot = dbPath;
        open();

        commands.put("create", new CreateCommand());
        commands.put("drop", new DropCommand());
        commands.put("use", new UseCommand());
        commands.put("show", new ShowCommand());

        commands.put("put", new PutCommand());
        commands.put("get", new GetCommand());
        commands.put("list", new ListCommand());
        commands.put("remove", new RemoveCommand());

        commands.put("exit", new ExitCommand());
    }

    public void open() throws ConnectionInterruptException {
        if (tables == null) {
            tables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRoot)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        FileMap table = new FileMap(file);
                        try {
                            table.load();
                        } catch (ConnectionInterruptException e) {
                            continue;
                        }
                        tables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                throw new ConnectionInterruptException("connection: unable to load the database");
            }
        }
    }

    @Override
    public void close() {
        if (tables != null) {
            try {
                for (FileMap table : tables.values()) {
                    table.unload();
                }
            } catch (ConnectionInterruptException e) {
                // suppress the exception
            }
        }
    }

    public String run(String argString) throws CommandInterruptException {
        String[] args = argString.trim().split("\\s+");
        Command command = commands.get(args[0]);
        if (command != null) {
            return command.execute(this, args);
        } else if (!args[0].equals("")) {
            throw new CommandInterruptException(args[0] + ": command not found");
        }
        return null;
    }
}
