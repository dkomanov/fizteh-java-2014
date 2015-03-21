package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;


import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Interpreter {
    private String dir;
    public Interpreter() throws Exception {
        dir = System.getProperty("fizteh.db.dir");
        TableManager table = new TableManager(dir);
        try {
            Scanner scanner = new Scanner(System.in);
            try {
                do {
                    System.out.print("$ ");
                    String[] input = scanner.nextLine().split(";");
                    for (int i = 0; i < input.length; ++i) {
                        if (input[i].length() > 0) {
                            String[] buffer = input[i].trim().split("\\s+");
                            try {
                                Parser.parse(buffer, table);
                            } catch (Exception exception) {
                                System.err.println(exception.getMessage());
                            }
                        }
                    }
                } while(true);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public Interpreter(String[] args) {
        TableManager table;
        List<String> cmdWithArgs = new ArrayList<String>();
        try {
            table = new TableManager(System.getProperty("fizteh.db.dir"));
            if (args.length > 0) {
                try {
                    StringBuilder helpArray = new StringBuilder();
                    for (int i = 0; i < args.length; ++i) {
                        helpArray.append(args[i]).append(' ');
                    }
                    String longStr = helpArray.toString();
                    String[] input = longStr.split(";");
                    for (int i = 0; i < input.length; ++i) {
                        if (input[i].length() > 0) {
                            String[] buffer = input[i].trim().split("\\s+");
                            try {
                                Parser.parse(buffer, table);
                            } catch (Exception exception) {
                                System.err.println(exception.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
}