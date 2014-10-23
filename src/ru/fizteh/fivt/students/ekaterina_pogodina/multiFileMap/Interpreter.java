package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap;

import ru.fizteh.fivt.students.ekaterina_pogodina.filemap.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Interpreter {
    private String dir;
    public Interpreter() throws Exception {
        List<String> cmdWithArgs = new ArrayList<String>();
        dir = System.getProperty("fizteh.db.dir");
        TableManager table = new TableManager(dir);
        try {
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
                            Parser.parse(arg, table);
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

    public Interpreter(String[] args) {
        TableManager table;
        List<String> cmdWithArgs = new ArrayList<String>();
        try {
            table = new TableManager(System.getProperty("fizteh.db.dir"));
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
                            Parser.parse(arg, table);
                            Table curTable = table.tables.get(table.currentTable);
                            for (int i = 0; i < 16; i++) {
                                for (int j = 0; j < 16; j++) {
                                    if (curTable.tableDateBase[i][j] != null) {
                                        curTable.tableDateBase[i][j].close();
                                    }
                                }
                            }
                            //table.close();
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
