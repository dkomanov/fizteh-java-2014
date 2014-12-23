package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandsPackage {
    private static final String PARAM_DELIMITER = "\\s+";
    public Command[] pack;

    public CommandsPackage(DataBase dataBase) {
        pack = new Command[] {
                new DatabaseCommand(dataBase, "create", 3, (database, arguments) -> {
                    String tableName = arguments[1];
                    String typesList = arguments[2].substring(1, arguments[2].length() - 1);
                    String[] typesNames = typesList.trim().split(PARAM_DELIMITER);
                    List<String> typesNamesList = new ArrayList<>(Arrays.asList(typesNames));
                    List<Class<?>> types = Utility.fillTypes(typesNamesList);
                    try {
                        if (database.createTable(tableName, types) != null) {
                            System.out.println("created");
                        } else {
                            System.out.println(tableName + " exists");
                            throw new IllegalStateException();
                        }
                    } catch (IOException io) {
                        System.err.println(Utility.IO_MSG + io.getMessage());
                        System.exit(1);
                    }
                }, (strings) -> {
                    String firstArgument = strings[1];
                    if (firstArgument.contains("(")) {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                    StringBuilder typesList = new StringBuilder();
                    int i = 0;
                    while ((i < strings.length) && !strings[i].contains("(")) {
                        i++;
                    }
                    if (i != 2 || (i == strings.length)) {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                    while (i < strings.length - 1) {
                        typesList.append(strings[i]);
                        typesList.append(" ");
                        i++;
                    }
                    typesList.append(strings[i]);
                    int length = typesList.length();
                    if (!(typesList.toString().charAt(0) == '(')
                            && !(typesList.toString().charAt(length - 1) == ')')) {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                    return new String[]
                            {strings[0], strings[1], typesList.toString()};
                }),
                new DatabaseCommand(dataBase, "drop", 2, (database, arguments) -> {
                    Table activeTable = database.getActiveTable();
                    String tableName = arguments[1];
                    if (!(activeTable == null) && activeTable.getName().equals(tableName)) {
                        database.setActiveTable(null);
                    }
                    try {
                        database.removeTable(tableName);
                        System.out.println("dropped");
                    } catch (IllegalStateException e) {
                        System.out.println(tableName + " not exists");
                    } catch (IOException io) {
                        System.err.println(Utility.IO_MSG + io.getMessage());
                        System.exit(1);
                    }
                }),
                new DatabaseCommand(dataBase, "use", 2, (database, arguments) -> {
                    String tableName = arguments[1];
                    Table newActiveTable = database.getTable(tableName);
                    Table activeTable = database.getActiveTable();
                    if (newActiveTable != null) {
                        if (activeTable != null) {
                            if (activeTable.getNumberOfUncommittedChanges() > 0) {
                                System.out.println(activeTable.getNumberOfUncommittedChanges()
                                        + " unsaved changes");
                                throw new IllegalStateException();
                            } else {
                                database.setActiveTable(newActiveTable);
                                System.out.println("using " + tableName);
                            }
                        } else {
                            database.setActiveTable(newActiveTable);
                            System.out.println("using " + tableName);
                        }
                    } else {
                        System.out.println(tableName + " not exists");
                        throw new IllegalStateException();
                    }
                }),
                new DatabaseCommand(dataBase, "show", 2, (database, arguments) -> {
                    List<String> keySet = database.getTableNames();
                    System.out.println("table_name row_count");
                    keySet.forEach((tableName) -> {
                        System.out.print(tableName + " ");
                        System.out.println(database.getTable(tableName).size());
                    });
                }, (strings) -> {
                    if (strings.length == 2 && strings[1].equals("tables")) {
                        return strings;
                    } else {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                }),
                new DatabaseTableCommand(dataBase, "put", 3, (database, arguments) -> {
                    String key = arguments[1];
                    String valueList = arguments[2];
                    Storeable oldValue = null;
                    Table activeTable = database.getActiveTable();
                    try {
                        oldValue = activeTable.put(key, database.deserialize(activeTable, valueList));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e.getMessage(), e);
                    }
                    if (oldValue == null) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                        System.out.println(database.serialize(activeTable, oldValue));
                    }

                }, (strings) -> {
                    String firstArgument = strings[1];
                    if (firstArgument.contains("[")) {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                    StringBuilder typesList = new StringBuilder();
                    int i = 0;
                    while ((i < strings.length) && !strings[i].contains("[")) {
                        i++;
                    }
                    if (i != 2 || i == strings.length) {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                    while (i < strings.length - 1) {
                        typesList.append(strings[i]);
                        typesList.append(" ");
                        i++;
                    }
                    typesList.append(strings[i]);
                    return new String[]{strings[0], strings[1], typesList.toString()};
                }),
                new DatabaseTableCommand(dataBase, "get", 2, (database, arguments) -> {
                    String key = arguments[1];
                    Storeable value = database.getActiveTable().get(key);
                    if (value == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("found");
                        System.out.println(database.serialize(database.getActiveTable(), value));
                    }

                }),
                new DatabaseTableCommand(dataBase, "remove", 2, (database, arguments) -> {
                    String key = arguments[1];
                    Storeable value = database.getActiveTable().remove(key);
                    if (value == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("removed");
                    }
                }),
                new DatabaseTableCommand(dataBase, "list", 1, (database, arguments) -> {
                    List<String> list = database.getActiveTable().list();
                    String joined = String.join(", ", list);
                    System.out.println(joined);

                }),
                new DatabaseTableCommand(dataBase, "size", 1,
                        (database, arguments) -> System.out.println(database.getActiveTable().size())
                ),
                new DatabaseTableCommand(dataBase, "commit", 1, (database, arguments) -> {
                    try {
                        System.out.println(database.getActiveTable().commit());
                    } catch (IOException io) {
                        System.err.println(Utility.IO_MSG + io.getMessage());
                        System.exit(1);
                    }
                }),
                new DatabaseTableCommand(dataBase, "rollback", 1,
                        (database, arguments) -> System.out.println(database.getActiveTable().rollback())
                ),
                new DatabaseCommand(dataBase, "exit", 1, (database, arguments) -> {
                    try {
                        if (database.getActiveTable() != null) {
                            database.getActiveTable().commit();
                        }
                    } catch (IOException io) {
                        System.err.println("Some i/o error occured " + io.getMessage());
                        System.exit(1);
                    }
                    database.close();
                    System.exit(0);
                })
        };
    }
}
