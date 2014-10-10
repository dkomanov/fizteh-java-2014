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
                for (int k = 0; k < commands.length; k++) {
                    int index;
                    int j;
                    index = 0;
                    j = 0;
                    for (int i = 0; i < commands[k].length(); i++) {
                        if (commands[k].charAt(i) == ' ') {
                            cmdWithArgs.add(commands[k].substring(index, i));
                            j++;
                            index = i + 1;
                        }
                    }
                    cmdWithArgs.add(commands[k].substring(index, commands[k].length()));
                    String[] arg = new String [cmdWithArgs.size()];
                    for (int i = 0; i < arg.length; i++) {
                        arg[i] = cmdWithArgs.get(i);
                    }
                    try {
                        Parser.parse(arg, dataBase);
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
                            for (int k = 0; k < commands.length; k++) {
                            boolean flag = false;
                            int index;
                            int j;
                            index = 0;
                            j = 0;
                            for (int i = 0; i < commands[k].length(); i++) {
                                if (commands[k].charAt(i) == ' ') {
                                    cmdWithArgs.add(commands[k].substring(index, i));
                                    j++;
                                    index = i + 1;
                                }
                            }
                            cmdWithArgs.add(commands[k].substring(index, commands[k].length()));
                            String[] arg = new String[cmdWithArgs.size()];
                            for (int i = 0; i < arg.length; i++) {
                                arg[i] = cmdWithArgs.get(i);
                            }
                            if (j != 0 && arg[1].equals("-r")) {
                                flag = true;
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
            //writeln ??
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}