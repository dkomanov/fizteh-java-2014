package ru.fizteh.fivt.students.IsalySultan.FileMap;

import java.util.Scanner;

public class PocketParser {
    
    PocketParser() {
     // Disable instantiation to this class.
    }

    public static void batchMode(Table object, String[] argv) {
        Scanner in = new Scanner(System.in);
        String comand = new String();
        int comandCount = 0;
        Comands newComand = new Comands();
        while (comandCount <= argv.length) {
            switch (argv[comandCount]) {
            case "put":
                newComand.put(argv[comandCount + 1], argv[comandCount + 2],
                        object);
                comandCount = comandCount + 3;
                break;
            case "get":
                newComand.get(argv[comandCount + 1], object);
                comandCount = comandCount + 2;
                break;
            case "remove":
                newComand.remove(argv[comandCount + 1], object);
                comandCount = comandCount + 2;
                break;
            case "list":
                newComand.list(object);
                comandCount = comandCount + 1;
                break;
            case "exit":
                object.writeFile();
                System.out.println("exit");
                return;
            default:
                System.out.println("команда не распознана");
            }
        }
    }
}
