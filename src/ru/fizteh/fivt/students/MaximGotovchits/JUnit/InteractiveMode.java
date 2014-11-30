package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class InteractiveMode {
    String [] cmdBuffer = new String[1024];
    boolean tableIsChosen = false; // Determines if table is chosen.
    Boolean isCommand = false;
    Scanner sc = new Scanner(System.in);
    String cmd = "";
    String tableName = new String();
    String dataBaseName = System.getProperty("fizteh.db.dir");
    static Map<String, String> storage = new HashMap<String, String>();
    void interactiveModeFunction() throws Exception {
        while (true) {
            System.out.print("$ ");
            cmd = sc.nextLine();
            cmd = cmd.replaceAll("\\s+", " ");
            if (cmd.equals("") || cmd.equals(" ")) {
                continue;
            }
            cmdBuffer = cmd.split(" ");
            if (cmdBuffer[0].equals("") && cmdBuffer.length > 0) {
                cmd = "";
                for (int ind = 1; ind < cmdBuffer.length; ++ind) {
                    cmd = cmd + cmdBuffer[ind] + " ";
                }
                cmdBuffer = cmd.split(" ");
            }
            if (cmdBuffer[0].equals("put")) {
                isCommand = true;
                if (tableIsChosen) {
                    if (cmdBuffer.length == 3) {
                        new ObjectTable().put(cmdBuffer[1], cmdBuffer[2]);
                    } else {
                        new ObjectTable().put(null, null);
                    }
                } else {
                    System.out.println("no table");
                }
            }
            if (cmdBuffer[0].equals("get")) {
                isCommand = true;
                if  (tableIsChosen) {
                    if (cmdBuffer.length == 2) {
                        new ObjectTable().get(cmdBuffer[1]);
                    } else {
                        new ObjectTable().get(null);
                    }
                } else {
                    System.out.println("no table");
                }
            }
            if (cmdBuffer[0].equals("remove") && cmdBuffer.length == 2) {
                isCommand = true;
                if (tableIsChosen) {
                    new ObjectTable().remove(cmdBuffer[1]);
                } else {
                    System.out.println("no table");
                }
            }
            if (cmdBuffer[0].equals("list")) {
                isCommand = true;
                if (tableIsChosen) {
                    new ObjectTable().list();
                } else {
                    System.out.println("no table");
                }
            }
            if (cmdBuffer[0].equals("exit")) {
                isCommand = true;
                if (tableIsChosen) {
                    if (new Exit().exitAvailable()) {
                        new FillTable().fillTableFunction(tableName);
                    } else {
                        continue;
                    }
                }
                sc.close();
                new Exit().exitFunction();
            }
            if (cmdBuffer[0].equals("create") && cmdBuffer.length == 2) {
                isCommand = true;
                new ObjectTableProvider().createTable(cmdBuffer[1]);
            }
            if (cmdBuffer[0].equals("drop") && cmdBuffer.length == 2) {
                isCommand = true;
                if (tableName.equals(cmdBuffer[1])) {
                    tableName = "";
                    tableIsChosen = false;
                }
                new ObjectTableProvider().removeTable(cmdBuffer[1]);
            }
            if (cmdBuffer[0].equals("use") && cmdBuffer.length == 2) {
                isCommand = true;
                if (storage.isEmpty()) {
                    new Use().useFunction(cmdBuffer[1], tableName);
                    if (tableIsChosen) {
                        if (new File(dataBaseName + "/" + cmdBuffer[1]).exists()) {
                            tableName = cmdBuffer[1];
                        }
                    }
                } else {
                    System.out.println(storage.size() + " unsaved changes");
                }
                tableIsChosen = true;
            }
            if (cmdBuffer[0].equals("show") && cmdBuffer[1].equals("tables")) {
                isCommand = true;
                new ShowTables().showTablesFunction(storage, tableName);
            }
            if (cmdBuffer[0].equals("rollback")) {
                isCommand = true;
                if (tableIsChosen) {
                   new ObjectTable().rollback();
                } else {
                    System.out.println("no table");
                }
            }
            if (cmdBuffer[0].equals("commit")) {
                isCommand = true;
                if (tableIsChosen) {
                    new ObjectTable().commit();
                } else {
                    System.out.println("no table");
                }
            }
            if (!isCommand) {
                System.err.println("incorrect syntax");
            }
            isCommand = false;
        }
    }
}
