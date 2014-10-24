package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util.*;

public class MultiMapMain{
    private MultiMapMain() {
        //
    }

    public static void main(final String[] args) {
        String rootDirectory = System.getProperty("fizteh.db.dir");
        Path rootDirectoryPath = Paths.get(rootDirectory);
        File root = new File(rootDirectory);
        if (!root.exists()) {
            System.err.println("You must specify fizteh.db.dir via -Ddb.file JVM parameter");
            System.exit(1);
        } else {
            if (!root.isDirectory()) {
                System.err.println(rootDirectory + " is not a directory");
                System.exit(1);
            } else {
                try {
                    TableHolder tableHolder = new TableHolder(rootDirectoryPath);
                    TableState tableState = new TableState(tableHolder);
                    new Interpreter(tableState, new Command[]{
                            new Command("create", 2, (TableState tableS, String[] arguments) -> {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String tableName = arguments[1];
                                if (map.containsKey(tableName)) {
                                    System.out.println(tableName + " exists");
                                } else {
                                    String pathTableDirectory = rootDirectoryPath.toAbsolutePath().toString()
                                            + File.separator
                                            + tableName;
                                    File tableDirectory = new File(pathTableDirectory);
                                    if (!tableDirectory.mkdir()) {
                                        System.err.println("Can't create table directory");
                                    }
                                    System.out.println("created");
                                    try {
                                        tableHolder.getTableMap().
                                                put(tableName,
                                                        new Table(rootDirectoryPath,
                                                                tableName));
                                        map.put(tableName, new HashMap<>());
                                    } catch (ExitException e) {
                                        System.err.println("Error while creating");
                                    }
                                }
                            }),
                            new Command("drop", 2, (TableState tableS, String[] arguments) ->
                            {
                                Map<String,  Map<String, String>> map = tableS.getMap();
                                String tableName = arguments[1];
                                if (!map.containsKey(tableName)) {
                                    System.out.println(tableName + " not exists");
                                } else {
                                    Path tableDirectory = tableHolder.getTableMap().get(tableName).getTablePath();
                                    try {
                                        File[] subDirectories = tableDirectory.toFile().listFiles();
                                        for (File directory: subDirectories) {
                                            try {
                                                File[] directoryFiles = directory.listFiles();
                                                for (File file : directoryFiles) {
                                                    try {
                                                        Files.delete(file.toPath());
                                                    } catch (IOException | SecurityException e) {
                                                        System.err.println(e);
                                                    }
                                                }
                                                Files.delete(directory.toPath());
                                            } catch (IOException | SecurityException e) {
                                                System.err.println(e);
                                            }
                                        }
                                        Files.delete(tableDirectory);
                                        System.out.println("dropped");
                                        tableHolder.getTableMap().remove(tableName);
                                        map.remove(tableName);
                                        if (tableS.getCurrentTableName().equals(tableName)) {
                                            tableS.setCurrentTableName("");
                                        }
                                    } catch (IOException | SecurityException e) {
                                        System.err.println(e);
                                    }
                                }
                            }),
                            new Command("use", 2, (TableState tableS, String[] arguments) ->
                            {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String tableName = arguments[1];
                                if (!tableName.isEmpty()) {
                                    if (!map.containsKey(tableName)) {
                                        System.out.println(tableName + " not exists");
                                    } else {
                                        System.out.println("using " + tableName);
                                        tableS.setCurrentTableName(tableName);
                                    }
                                }
                            }),
                            new Command("show tables", 1, (TableState tableS, String[] arguments) ->
                            {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                System.out.println("table_name row_count");
                                for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                                    System.out.print(entry.getKey() + " ");
                                    System.out.println(entry.getValue().size());
                                }
                            }),
                            new Command("put", 3, (TableState tableS, String[] arguments) ->
                            {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String currentTableName = tableS.getCurrentTableName();
                                if (currentTableName.equals("")) {
                                    System.out.println("no table");
                                } else {
                                    Map<String, String> currentStorage = map.get(currentTableName);
                                    if (!arguments[1].isEmpty() && !arguments[2].isEmpty()) {
                                        String key = arguments[1];
                                        String value = arguments[2];
                                        if (!currentStorage.containsKey(key)) {
                                            System.out.println("new");
                                        } else {
                                            System.out.println("overwrite");
                                            System.out.println(currentStorage.get(key));
                                            currentStorage.remove(key);
                                        }
                                        currentStorage.put(key, value);
                                    }
                                }
                            }),
                            new Command("get", 2, (TableState tableS, String[] arguments) ->
                            {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String currentTableName = tableS.getCurrentTableName();
                                if (currentTableName.equals("")) {
                                    System.out.println("no table");
                                } else {
                                    Map<String, String> currentStorage = map.get(currentTableName);
                                    if (!arguments[1].isEmpty()) {
                                        String key = arguments[1];
                                        if (currentStorage.containsKey(key)) {
                                            System.out.println("found");
                                            System.out.println(currentStorage.get(key));
                                        } else {
                                            System.out.println("not found");
                                        }
                                    }
                                }
                            }),
                            new Command("remove", 2, (TableState tableS, String[] arguments) ->
                            {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String currentTableName = tableS.getCurrentTableName();
                                if (currentTableName.equals("")) {
                                    System.out.println("no table");
                                } else {
                                    Map<String, String> currentStorage = map.get(currentTableName);
                                    if (!arguments[1].isEmpty()) {
                                        String key = arguments[1];
                                        if (currentStorage.containsKey(key)) {
                                            currentStorage.remove(key);
                                            System.out.println("removed");
                                        } else {
                                            System.out.println("not found");
                                        }
                                    }
                                }
                            }),
                            new Command("list", 1, (TableState tableS, String[] arguments) ->
                            {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String currentTableName = tableS.getCurrentTableName();
                                if (currentTableName.equals("")) {
                                    System.out.println("no table");
                                } else {
                                    Map<String, String> currentStorage = map.get(currentTableName);
                                    Set keySet = currentStorage.keySet();
                                    int size = keySet.size();
                                    int counter = 0;
                                    Iterator it = keySet.iterator();
                                    while (it.hasNext()) {
                                        if (counter == size - 1) {
                                            System.out.print(it.next());
                                        } else {
                                            System.out.print(it.next() + ", ");
                                        }
                                        counter++;
                                    }
                                    System.out.println();
                                }
                            }),
                            new Command("exit", 1, (TableState tableS, String[] arguments) ->
                            {
                                try {
                                    tableHolder.close();
                                    System.out.println("exit");
                                    System.exit(0);
                                } catch (Exception t) {
                                    System.exit(-1);
                                }
                            })
                    }).run(args);
                } catch (ExitException e) {
                    System.exit(e.getStatus());
                }
            }
        }
    }
}
