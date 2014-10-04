package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author AlexeyZhuravlev
 */
public abstract class Command {

    public static Command fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("Empty command");
        }
        String[] tokens = s.replaceFirst(" *", "").split(" ", 0);
        switch (tokens[0]) {
            case "put":
                if (tokens.length != 3) {
                    throw new Exception("Unexpected number of arguments for put command: 2 required");
                } else {
                    return new PutCommand(tokens[1], tokens[2]);
                }
            case "get":
                if (tokens.length != 2) {
                    throw new Exception("Unexpected number of arguments for get command: 1 required");
                } else {
                    return new GetCommand(tokens[1]);
                }
            case "remove":
                if (tokens.length != 2) {
                    throw new Exception("Unexpected number of arguments for remove command: 1 required");
                } else {
                    return new RemoveCommand(tokens[1]);
                }
            case "list":
                if (tokens.length != 1) {
                    throw new Exception("Unexpected number of arguments for list command: 0 required");
                } else {
                    return new ListCommand();
                }
            case "exit":
                if (tokens.length != 1) {
                    throw new Exception("Unexpected number of arguments for exit command: 0 required");
                } else {
                    return new ExitCommand();
                }
            default:
                throw new Exception(tokens[0] + " is unknown command");
        }
    }

    public abstract void execute(DataBase base, AtomicBoolean exitStatus) throws Exception;
}
