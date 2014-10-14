package ru.fizteh.fivt.students.ekaterina_pogodina.filemap;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class Main {
    private Main() {
        //
    }

    public static void main(final String[] args) {
        List<String> cmdWithArgs = new ArrayList<String>();
        if (args.length > 0) {
            try {
                DataBase dataBase = new DataBase(System.getProperty("db.file"));
                String line = "";
                for (int i = 0; i < args.length; i++) {
                    line = line.concat(args[i]);
                    if (i != args.length - 1) {
                        line = line.concat(" ");
                    }
                }
                String[] commands = line.split(" ; ");
                for (String command: commands) {
                    int index;
                    index = 0;
                    for (int i = 0; i < command.length(); i++) {
                        if (command.charAt(i) == ' ') {
                            cmdWithArgs.add(command.substring(index, i));
                            index = i + 1;
                        }
                    }
                    cmdWithArgs.add(command.substring(index, command.length()));
                    String[] arg = new String [cmdWithArgs.size()];
                    for (int i = 0; i < arg.length; i++) {
                        arg[i] = cmdWithArgs.get(i);
                    }
                    try {
                        Parser.parse(arg, dataBase);
                        dataBase.close();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                    cmdWithArgs.clear();
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } else {
            DataBase dataBase;
            try {
                dataBase = new DataBase(System.getProperty("db.file"));
                try (Scanner sc = new Scanner(System.in)) {
                    while (true) {
                        System.out.print("$ ");
                        String line = "";
                        if (sc.hasNext()) {
                            line = sc.nextLine();
                        } else {
                            System.exit(0);
                        }
                        String[] commands = line.split(" ; ");
                        for (String command: commands) {
                            int index;
                            index = 0;
                            for (int i = 0; i < command.length(); i++) {
                                if (command.charAt(i) == ' ') {
                                    cmdWithArgs.add(command.substring(index, i));
                                    index = i + 1;
                                }
                            }
                            cmdWithArgs.add(command.substring(index, command.length()));
                            String[] arg = new String[cmdWithArgs.size()];
                            for (int i = 0; i < arg.length; i++) {
                                 arg[i] = cmdWithArgs.get(i);
                            }
                            try {
                                Parser.parse(arg, dataBase);
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                            cmdWithArgs.clear();
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}

