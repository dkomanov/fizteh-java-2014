package ru.fizteh.fivt.students.isalysultan.FileMap;

import java.io.IOException;

public class Parser {
   public void myParser(Table tables, String[] command) throws IOException {
        Commands newCommand = new Commands();
        switch (command[0]) {
        case "put":
            newCommand.put(command[1], command[2],
                    tables);
            break;
        case "get":
            newCommand.get(command[1], tables);
            break;
        case "remove":
            newCommand.remove(command[1], tables);
            break;
        case "list":
            newCommand.list(tables);
            break;
        case "exit":
            tables.writeFile();
            System.out.println("exit");
            return;
        default:
            System.out.println(" Command is not recognized.");
        }
    }

}
