package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BatchMode {
    boolean tablesIsChosen = false; // Determines if table is chosen.
    Path dataBaseName = Paths.get(System.getProperty("fizteh.db.dir"));
    String [] cmdBuffer = new String[1024];
    Boolean isCommand = false;
    String tableName;
    static Map<String, String> storage = new HashMap<String, String>();
    void batchModeFunction(String commands) throws Exception {
        commands = commands.replaceAll("\\s+" + ";" + "\\s+", ";");
        commands = commands.replaceAll("\\s+", " ");
        commands = commands.replaceAll(";", " ");
        commands = commands.replaceAll("\\s+", " ");
        String [] cmdBuffer = commands.split(" ");
        System.out.println(commands);
        int parseInd = 0;
        while (parseInd < cmdBuffer.length) {
            if (cmdBuffer[parseInd].equals("put")) {
                isCommand = true;
                if (tablesIsChosen) {
                    if ((cmdBuffer.length - parseInd) > 2) {
                        new ObjectTable().put(cmdBuffer[parseInd + 1], cmdBuffer[parseInd + 2]);
                    } else {
                        new ObjectTable().put(null, null);
                    }
                } else {
                    System.out.println("no table");
                }
                parseInd += 3;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("get") && (cmdBuffer.length - parseInd) > 1) {
                isCommand = true;
                if  (tablesIsChosen) {
                    if (cmdBuffer.length - parseInd > 1) {
                        new ObjectTable().get(cmdBuffer[parseInd + 1]);
                    } else {
                        new ObjectTable().get(null);
                    }
                } else {
                    System.out.println("no table");
                }
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("remove") && (cmdBuffer.length - parseInd) > 1) {
                isCommand = true;
                if (tablesIsChosen) {
                    new ObjectTable().remove(cmdBuffer[parseInd + 1]);
                } else {
                    System.out.println("no table");
                }
                parseInd += 2;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("list")) {
                isCommand = true;
                if (tablesIsChosen) {
                    new ObjectTable().list();
                } else {
                    System.out.println("no table");
                }
                ++parseInd;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("exit")) {
                isCommand = true;
                new ObjectTable().commit();
                new Exit().exitFunction();
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("create") && (cmdBuffer.length - parseInd) > 1) {
                isCommand = true;
                new ObjectTableProvider().createTable(cmdBuffer[parseInd + 1]);
                parseInd += 2;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("drop") && (cmdBuffer.length - parseInd) > 1) {
                isCommand = true;
                if (tableName.equals(cmdBuffer[parseInd + 1])) {
                    tableName = "";
                    tablesIsChosen = false;
                }
                new ObjectTableProvider().removeTable(cmdBuffer[parseInd + 1]);
                parseInd += 2;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("use") && (cmdBuffer.length - parseInd) > 1) {
                isCommand = true;
                if (storage.isEmpty()) {
                    new Use().useFunction(cmdBuffer[parseInd + 1], tableName);
                    if (tablesIsChosen) {
                        if (new File(dataBaseName.getFileName() + "/" + cmdBuffer[parseInd + 1]).exists()) {
                            tableName = cmdBuffer[parseInd + 1];
                        }
                    }
                } else {
                    System.out.println(storage.size() + " unsaved changes");
                }
                tablesIsChosen = true;
                parseInd += 2;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("show") && cmdBuffer[parseInd + 1].equals("tables")) {
                isCommand = true;
                new ShowTables().showTablesFunction(storage, tableName);
                parseInd += 2;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("rollback")) {
                isCommand = true;
                if (tablesIsChosen) {
                    new ObjectTable().rollback();
                } else {
                    System.out.println("no table");
                }
                ++parseInd;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (cmdBuffer[parseInd].equals("commit")) {
                isCommand = true;
                new ObjectTable().commit();
                ++parseInd;
            }
            checkIndex(cmdBuffer.length, parseInd);
            if (!isCommand) {
                System.err.println("incorrect syntax");
            }
            isCommand = false;
        }
    }
    void checkIndex(int buffLength, int parseLength) {
        if (buffLength <= parseLength) {
            new ObjectTable().commit();
            new Exit().exitFunction();
        }
    }
}
