package ru.fizteh.fivt.students.moskupols.filemap;

import ru.fizteh.fivt.students.moskupols.cliutils.*;

/**
 * Created by moskupols on 28.09.14.
 */
public class FileMapCommandFabric implements CommandFabric {
    private final DataBase db;

    public FileMapCommandFabric(DataBase db) {
        this.db = db;
    }

    @Override
    public Command fromString(String s) throws UnknownCommandException {
        final String[] tokens = s.trim().split(" ");

        if ("put".equals(tokens[0])) {
            return new Command() {
                @Override
                public void execute() throws CommandExecutionException, StopProcessingException {
                    if (tokens.length != 3) {
                        throw new CommandExecutionException("Unexpected number of arguments");
                    }
                    String old = db.map.put(tokens[1], tokens[2]);
                    if (null == old) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                        System.out.println(old);
                    }
                }
            };
        }

        if ("get".equals(tokens[0])) {
            return new Command() {
                @Override
                public void execute() throws CommandExecutionException, StopProcessingException {
                    if (tokens.length != 2) {
                        throw new CommandExecutionException("Unexpected number of arguments");
                    }
                    String value = db.map.get(tokens[1]);
                    System.out.println(value == null ? "not found" : value);
                }
            };
        }

        if ("remove".equals(tokens[0])) {
            return new Command() {
                @Override
                public void execute() throws CommandExecutionException, StopProcessingException {
                    if (tokens.length != 2) {
                        throw new CommandExecutionException("Unexpected number of arguments");
                    }
                    System.out.println(db.map.remove(tokens[1]) == null ? "not found" : "removed");
                }
            };
        }

        if ("list".equals(tokens[0])) {
            return new Command() {
                @Override
                public void execute() throws CommandExecutionException, StopProcessingException {
                    if (tokens.length != 1) {
                        throw new CommandExecutionException("Unexpected number of arguments");
                    }
                    for (String k : db.map.keySet()) {
                        System.out.println(k);
                    }
                }
            };
        }

        if ("exit".equals(tokens[0])) {
            return new Command() {
                @Override
                public void execute() throws CommandExecutionException, StopProcessingException {
                    throw new StopProcessingException();
                }
            };
        }

        throw new UnknownCommandException(tokens[0]);
    }
}
