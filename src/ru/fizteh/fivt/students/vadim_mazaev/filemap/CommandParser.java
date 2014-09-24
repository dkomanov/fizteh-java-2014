package ru.fizteh.fivt.students.vadim_mazaev.filemap;

import java.util.NoSuchElementException;
import java.util.Scanner;

import ru.fizteh.fivt.students.vadim_mazaev.filemap.commands.GetCommand;
import ru.fizteh.fivt.students.vadim_mazaev.filemap.commands.ListCommand;
import ru.fizteh.fivt.students.vadim_mazaev.filemap.commands.PutCommand;
import ru.fizteh.fivt.students.vadim_mazaev.filemap.commands.RemoveCommand;

public final class CommandParser {
    private CommandParser() {
        //not called
    }

    public static void packageMode(final DbConnector link,
            final String[] args) throws ThrowExit {
        StringBuilder builder = new StringBuilder();
        for (String current : args) {
            builder.append(current);
            builder.append(" ");
        }
        String[] cmds = builder.toString().split(";");
        for (String current : cmds) {
            parse(link, current.trim().split("\\s+"), true);
        }
    }

    public static void interactiveMode(final DbConnector link)
            throws ThrowExit {
        String[] cmds;
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                cmds = in.nextLine().trim().split(";");
                for (String current : cmds) {
                    parse(link, current.trim().split("\\s+"), false);
                }
            }
        } catch (NoSuchElementException e) {
            throw new ThrowExit(false);
        }
    }

    public static void parse(final DbConnector link,
        final String[] command, final boolean exitOnError) throws ThrowExit {
        try {
            if (command.length != 0) {
                switch(command[0]) {
                case "put":
                    PutCommand put = new PutCommand(link);
                    put.execute(command);
                    break;
                case "get":
                    GetCommand get = new GetCommand(link);
                    get.execute(command);
                    break;
                case "remove":
                    RemoveCommand remove = new RemoveCommand(link);
                    remove.execute(command);
                    break;
                case "list":
                    ListCommand list = new ListCommand(link);
                    list.execute(command);
                    break;
                case "exit":
                    throw new ThrowExit(true);
                default:
                    throw new
                        IllegalArgumentException("No such command declared");
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            if (exitOnError) {
                throw new ThrowExit(false);
            }
        }
    }
}
