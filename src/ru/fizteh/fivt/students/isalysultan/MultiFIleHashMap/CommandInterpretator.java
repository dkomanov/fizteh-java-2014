package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;

public class CommandInterpretator {

    public void executeCommand(RootDirectory direct, String[] command)
            throws IOException {
        switch (command[0]) {
        case "create":
            Command.create(direct, command[1]);
            break;
        case "use":
            Command.use(direct, command[1]);
            break;
        case "drop":
            Command.drop(direct, command[1]);
            break;
        case "show":
            if (command[1].equals("tables")) {
                Command.show(direct);
            } else {
                System.out.println("Command is not recognized");
            }
        case "put":
            direct.executePut(command[1], command[2]);
            break;
        case "get":
            direct.executeGet(command[1]);
            break;
        case "remove":
            direct.executeRemove(command[1]);
        case "list":
            direct.executeList();
        case "exit":
            direct.executeExit();
            return;
        default:
            System.out.println("Command is not recognized.");
        }
    }

}
