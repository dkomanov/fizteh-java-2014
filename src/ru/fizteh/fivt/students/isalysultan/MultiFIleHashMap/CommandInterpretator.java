package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;

public class CommandInterpretator {

    public void executeCommand(RootDirectory direct, String[] command)
            throws IOException {
        switch (command[0]) {
        case "create":
            if (command.length < 2 || command.length > 2) {
                System.err.println("Wrong arguments");
            }
            Command.create(direct, command[1]);
            break;
        case "use":
            if (command.length < 2 || command.length > 2) {
                System.err.println("Wrong arguments");
            }
            Command.use(direct, command[1], true);
            break;
        case "drop":
            if (command.length < 2 || command.length > 2) {
                System.err.println("Wrong arguments");
            }
            Command.drop(direct, command[1]);
            break;
        case "show":
            if (command[1].equals("tables")) {
                Command.show(direct);
                break;
            } else {
                System.out.println("Command is not recognized");
                break;
            }
        case "put":
            if (command.length < 3 || command.length > 3) {
                System.err.println("Wrong arguments");
            }
            boolean useTable = direct.executePut(command[1], command[2]);
            if (!useTable) {
                System.out.println("no table");
            }
            break;
        case "get":
            if (command.length < 2 || command.length > 2) {
                System.err.println("Wrong arguments");
            }
            useTable = direct.executeGet(command[1]);
            if (!useTable) {
                System.out.println("no table");
            }
            break;
        case "remove":
            if (command.length < 2 || command.length > 2) {
                System.err.println("Wrong arguments");
            }
            useTable = direct.executeRemove(command[1]);
            if (!useTable) {
                System.out.println("no table");
            }
            break;
        case "list":
            useTable = direct.executeList();
            if (!useTable) {
                System.out.println("no table");
            }
            break;
        case "exit": {
            direct.executeExit();
            System.exit(0);
            break;
        }
        default:
            System.out.println("Command is not recognized.");
        }
    }

}
