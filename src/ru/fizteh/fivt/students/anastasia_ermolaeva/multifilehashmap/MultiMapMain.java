package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util.*;

public class MultiMapMain {
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
                try (TableHolder tableHolder = new TableHolder(rootDirectoryPath)) {
                    TableState tableState = new TableState(tableHolder);
                    new Interpreter(tableState, new Command[] {
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
                                        System.exit(1);
                                    } else {
                                        System.out.println("created");
                                        map.put(tableName, new HashMap<>());
                                    }
                                }
                            }),
                            new Command("drop", 2, (TableState tableS, String[] arguments) -> {
                                Map<String,  Map<String, String>> map = tableS.getMap();
                                String tableName = arguments[1];
                                if (!map.containsKey(tableName)) {
                                    System.out.println(tableName + " not exists");
                                } else {
                                    Path tableDirectory = Paths.get(
                                        tableHolder.getRootPath().toAbsolutePath().toString()
                                            + File.separator + tableName);
                                    String [] subDirs = tableDirectory.toFile().list();
                                    if (subDirs.length != 0) {
                                        File[] subDirectories = tableDirectory.toFile().listFiles();
                                        for (File directory : subDirectories) {
                                            try {
                                                File[] directoryFiles = directory.listFiles();
                                                for (File file : directoryFiles) {
                                                    try {
                                                        Files.delete(file.toPath());
                                                    } catch (IOException | SecurityException e) {
                                                        System.err.println(e);
                                                        System.exit(1);
                                                    }
                                                }
                                                Files.delete(directory.toPath());
                                            } catch (IOException | SecurityException e) {
                                                System.err.println(e);
                                                System.exit(1);
                                            }
                                        }
                                    }
                                    try {
                                        Files.delete(tableDirectory);
                                        System.out.println("dropped");
                                        map.remove(tableName);
                                        if (tableS.getCurrentTableName().equals(tableName)) {
                                            tableS.setCurrentTableName("");
                                        }
                                    } catch (IOException e) {
                                        System.err.println(e);
                                        System.exit(1);
                                    }
                                }
                            }),
                            new Command("use", 2, (TableState tableS, String[] arguments) -> {
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
                            new Command("show tables", 1, (TableState tableS, String[] arguments) -> {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                System.out.println("table_name row_count");
                                for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                                    System.out.print(entry.getKey() + " ");
                                    System.out.println(entry.getValue().size());
                                }
                            }),
                            new Command("put", 3, (TableState tableS, String[] arguments) -> {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String currentTableName = tableS.getCurrentTableName();
                                if (tableS.checkCurrentTable()) {
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
                            new Command("get", 2, (TableState tableS, String[] arguments) -> {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String currentTableName = tableS.getCurrentTableName();
                                if (tableS.checkCurrentTable()) {
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
                            new Command("remove", 2, (TableState tableS, String[] arguments) -> {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String currentTableName = tableS.getCurrentTableName();
                                if (tableS.checkCurrentTable()) {
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
                            new Command("list", 1, (TableState tableS, String[] arguments) -> {
                                Map<String, Map<String, String>> map = tableS.getMap();
                                String currentTableName = tableS.getCurrentTableName();
                                if (tableS.checkCurrentTable()) {
                                    Map<String, String> currentStorage = map.get(currentTableName);
                                    List<String> list = new ArrayList<>(currentStorage.keySet());
                                    String joined = String.join(", ", list);
                                    System.out.println(joined);
                                }
                            }),
                            new Command("exit", 1, (TableState tableS, String[] arguments) -> {
                                try {
                                    for (Map.Entry<String, Map<String, String>> entry: tableS.getMap().entrySet()) {
                                        tableHolder.getTableMap().put(entry.getKey(),
                                                new Table(rootDirectoryPath,
                                                        entry.getKey(), entry.getValue()));
                                    }
                                    tableHolder.close();
                                    System.out.println("exit");
                                    System.exit(0);
                                } catch (ExitException e) {
                                    System.out.println("exit");
                                    System.exit(e.getStatus());
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
