package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

import java.util.NoSuchElementException;
import java.util.Scanner;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.CreateCommand;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.DropCommand;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.ExitCommand;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.GetCommand;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.ListCommand;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.PutCommand;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.RemoveCommand;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.ShowTablesCommand;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands.UseCommand;

public final class CommandParser {
    private CommandParser() {
        //Not called, only for checkstyle.
    }

    public static void batchMode(TableManager manager,
            String[] args) throws ThrowExit {
        StringBuilder builder = new StringBuilder();
        for (String current : args) {
            builder.append(current);
            builder.append(" ");
        }
        String[] cmds = builder.toString().split(";");
        for (String current : cmds) {
            parse(manager, current.trim().split("\\s+"), true);
        }
        ExitCommand.execute(manager);
    }

    public static void interactiveMode(TableManager manager)
            throws ThrowExit {
        String[] cmds;
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                cmds = in.nextLine().trim().split(";");
                for (String current : cmds) {
                    parse(manager, current.trim().split("\\s+"), false);
                }
            }
        } catch (NoSuchElementException e) {
            throw new ThrowExit(false);
        }
    }

    private static void parse(TableManager link,
        String[] command, boolean exitOnError) throws ThrowExit {
        try {
            if (command.length > 0 && !command[0].isEmpty()) {
                switch(command[0]) {
                case "use":
                    UseCommand use = new UseCommand(link);
                    use.execute(command);
                    break;
                case "create":
                    CreateCommand create = new CreateCommand(link);
                    create.execute(command);
                    break;
                case "drop":
                    DropCommand drop = new DropCommand(link);
                    drop.execute(command);
                    break;
                case "show":
                    if (command.length > 1 && command[1].equals("tables")) {
                        ShowTablesCommand showTables = new ShowTablesCommand(link);
                        showTables.execute(command);
                    } else {
                        throw new
                            IllegalArgumentException("No such command declared");
                    }
                    break;
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
                    ExitCommand.execute(link);
                    break;
                default:
                    throw new
                        IllegalArgumentException("No such command declared");
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            } else {
                System.err.println("Something went wrong. Unexpected error");
                throw new ThrowExit(false);
            }
            if (exitOnError) {
                throw new ThrowExit(false);
            }
        }
    }
}
