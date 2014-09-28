package ru.fizteh.fivt.students.andreyzakharov.filemap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DbConnector implements AutoCloseable {
    Map<String, Command> commands = new HashMap<>();
    FileMap db;

    DbConnector(Path dbPath) throws ConnectionInterruptException {
        if (!Files.exists(dbPath)) {
            throw new ConnectionInterruptException("connection: file does not exist");
        }
        if (Files.isDirectory(dbPath)) {
            throw new ConnectionInterruptException("connection: destination is a directory");
        }
        db = new FileMap(dbPath);

        commands.put("put", new PutCommand());
        commands.put("get", new GetCommand());
        commands.put("list", new ListCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("exit", new ExitCommand());
    }

    @Override
    public void close() {
        try {
            db.unload();
        } catch (ConnectionInterruptException e) {
            // suppress the exception
        }
    }

    public String run(String argString) throws CommandInterruptException, ConnectionInterruptException {
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
