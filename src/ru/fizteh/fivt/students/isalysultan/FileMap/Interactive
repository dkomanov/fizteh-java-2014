package ru.fizteh.fivt.students.IsalySultan.FileMap;

import java.util.Scanner;

public class Interactive {

    private Interactive() {
       // Disable instantiation to this class.
    }

    public static void interactiveMode(table tables) {
        Scanner in = new Scanner(System.in);
        String[] parserComand;
        comands object = new comands();
        while (true) {
            String comand;
            comand = in.nextLine();
            parserComand = comand.split(" ");
            switch (parserComand[0]) {
            case "put":
                object.put(parserComand[1], parserComand[2], tables);
                break;
            case "get":
                object.get(parserComand[1], tables);
                break;
            case "remove":
                object.remove(parserComand[1], tables);
                break;
            case "list":
                object.list(tables);
                break;
            case "exit":
                tables.writeFile();
                System.out.println("exit");
                return;
            default:
                System.out.println("команда не распознана");
            }
        }
    }
}
