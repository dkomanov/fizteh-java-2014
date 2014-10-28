package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            TableProvider database = new TableProvider(System.getProperty("fizteh.db.dir"));
            if (args.length == 0) {
                interactiveMode(database);
            } else {
                batchMode(args, database);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void interactiveMode(TableProvider database) throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("$ ");
        while (input.hasNextLine()) {
            try {
                Command.exec(Command.parseCmd(input.nextLine()), database);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.print("$ ");
        }
        input.close(); 
    }

    public static void batchMode(String[] args, TableProvider database) throws Exception {
        String[] input = Command.parseInput(args);
        for (String cmd : input) {
            Command.exec(Command.parseCmd(cmd), database);
        }
    }
}
