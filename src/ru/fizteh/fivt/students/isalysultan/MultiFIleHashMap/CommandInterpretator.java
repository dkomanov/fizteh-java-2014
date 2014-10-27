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
            direct.executePut(command[1], command[2]);
            break;
        case "get":
            if (command.length < 2 || command.length > 2) {
                System.err.println("Wrong arguments");
            }
            direct.executeGet(command[1]);
            break;
        case "remove":
            if (command.length < 2 || command.length > 2) {
                System.err.println("Wrong arguments");
            }
            direct.executeRemove(command[1]);
            break;
        case "list":
            direct.executeList();
            break;
        case "exit":
            direct.executeExit();
            System.exit(0);
        default:
            System.out.println("Command is not recognized.");
        }
    }

}
