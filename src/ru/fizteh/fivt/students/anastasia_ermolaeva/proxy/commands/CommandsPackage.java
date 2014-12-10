package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.TableHolder;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandsPackage {
    private static final String PARAM_DELIMITER = "\\s+";
    public Command[] pack;

    public CommandsPackage(TableHolder tableHolder) {
        pack = new Command[]{
                new DatabaseCommand(tableHolder, "create", 3, (holder, arguments) -> {
                    String tableName = arguments[1];
                    String typesList = arguments[2].substring(1, arguments[2].length() - 1);
                    String[] typesNames = typesList.trim().split(PARAM_DELIMITER);
                    List<String> typesNamesList = new ArrayList<>(Arrays.asList(typesNames));
                    List<Class<?>> types = Utility.fillTypes(typesNamesList);
                    try {
                        if (holder.createTable(tableName, types) != null) {
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
                new DatabaseCommand(tableHolder, "drop", 2, (holder, arguments) -> {
                    Table activeTable = holder.getActiveTable();
                    String tableName = arguments[1];
                    if (!(activeTable == null) && activeTable.getName().equals(tableName)) {
                        holder.setActiveTable(null);
                    }
                    try {
                        holder.removeTable(tableName);
                        System.out.println("dropped");
                    } catch (IllegalStateException e) {
                        System.out.println(tableName + " not exists");
                    } catch (IOException io) {
                        System.err.println(Utility.IO_MSG + io.getMessage());
                        System.exit(1);
                    }
                }),
                new DatabaseCommand(tableHolder, "use", 2, (holder, arguments) -> {
                    String tableName = arguments[1];
                    Table newActiveTable = holder.getTable(tableName);
                    Table activeTable = holder.getActiveTable();
                    if (newActiveTable != null) {
                        if (activeTable != null) {
                            if (activeTable.getNumberOfUncommittedChanges() > 0) {
                                System.out.println(activeTable.getNumberOfUncommittedChanges()
                                        + " unsaved changes");
                                throw new IllegalStateException();
                            } else {
                                holder.setActiveTable(newActiveTable);
                                System.out.println("using " + tableName);
                            }
                        } else {
                            holder.setActiveTable(newActiveTable);
                            System.out.println("using " + tableName);
                        }
                    } else {
                        System.out.println(tableName + " not exists");
                        throw new IllegalStateException();
                    }
                }),
                new DatabaseCommand(tableHolder, "show", 2, (holder, arguments) -> {
                    List<String> keySet = holder.getTableNames();
                    System.out.println("table_name row_count");
                    keySet.forEach((tableName) -> {
                        System.out.print(tableName + " ");
                        System.out.println(holder.getTable(tableName).size());
                    });
                }, (strings) -> {
                    if (strings.length == 2 && strings[1].equals("tables")) {
                        return strings;
                    } else {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                }),
                new DatabaseTableCommand(tableHolder, "put", 3, (holder, arguments) -> {
                    String key = arguments[1];
                    String valueList = arguments[2];
                    Storeable oldValue = null;
                    Table activeTable = holder.getActiveTable();
                    try {
                        oldValue = activeTable.put(key, holder.deserialize(activeTable, valueList));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e.getMessage(), e);
                    }
                    if (oldValue == null) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                        System.out.println(holder.serialize(activeTable, oldValue));
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
                new DatabaseTableCommand(tableHolder, "get", 2, (holder, arguments) -> {
                    String key = arguments[1];
                    Storeable value = holder.getActiveTable().get(key);
                    if (value == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("found");
                        System.out.println(holder.serialize(holder.getActiveTable(), value));
                    }

                }),
                new DatabaseTableCommand(tableHolder, "remove", 2, (holder, arguments) -> {
                    String key = arguments[1];
                    Storeable value = holder.getActiveTable().remove(key);
                    if (value == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("removed");
                    }
                }),
                new DatabaseTableCommand(tableHolder, "list", 1, (holder, arguments) -> {
                    List<String> list = holder.getActiveTable().list();
                    String joined = String.join(", ", list);
                    System.out.println(joined);

                }),
                new DatabaseTableCommand(tableHolder, "size", 1,
                        (holder, arguments) -> System.out.println(tableHolder.getActiveTable().size())
                ),
                new DatabaseTableCommand(tableHolder, "commit", 1, (holder, arguments) -> {
                    try {
                        System.out.println(holder.getActiveTable().commit());
                    } catch (IOException io) {
                        System.err.println(Utility.IO_MSG + io.getMessage());
                        System.exit(1);
                    }
                }),
                new DatabaseTableCommand(tableHolder, "rollback", 1,
                        (holder, arguments) -> System.out.println(holder.getActiveTable().rollback())
                ),
                new DatabaseCommand(tableHolder, "exit", 1, (holder, arguments) -> {
                    try {
                        if (holder.getActiveTable() != null) {
                            holder.getActiveTable().commit();
                        }
                    } catch (IOException io) {
                        System.err.println("Some i/o error occured " + io.getMessage());
                        System.exit(1);
                    }
                    holder.close();
                    System.exit(0);
                })
        };
    }
}
