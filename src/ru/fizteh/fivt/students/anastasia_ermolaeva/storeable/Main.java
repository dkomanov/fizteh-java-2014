package ru.fizteh.fivt.students.anastasia_ermolaeva.storeable;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.ArgsListCommand;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Command;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.DBState;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.DatabaseFormatException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.ExitException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(final String[] args) {
        String rootDirectory = System.getProperty("fizteh.db.dir");
        if (rootDirectory == null) {
            System.err.println("You must specify fizteh.db.dir via JVM parameter");
            System.exit(1);
        }
        TableProviderFactory factory = new TableHolderFactory();
        try {
            start(new DBState(factory.create(rootDirectory)), args);
        } catch (DatabaseFormatException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException io) {
            System.err.println(Utility.IO_MSG + io.getMessage());
            System.exit(1);
        } catch (ExitException t) {
            System.exit(t.getStatus());
        }
    }

    public static void start(DBState tableState, final String[] args) throws ExitException {
        new Interpreter(tableState, new Command[] {
                new ArgsListCommand("create", 3, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String tableName = arguments[1];
                    String typesList = arguments[2].substring(1, arguments[2].length() - 1);
                    String[] typesNames = typesList.trim().split(Interpreter.PARAM_DELIMITER);
                    List<String> typesNamesList = new ArrayList<>(Arrays.asList(typesNames));
                    List<Class<?>> types = Utility.fillTypes(typesNamesList);
                    try {
                        if (provider.createTable(tableName, types) != null) {
                            System.out.println("created");
                        } else {
                            System.out.println(tableName + " exists");
                            throw new IllegalStateException();
                        }
                    } catch (IOException io) {
                        System.err.println(Utility.IO_MSG + io.getMessage());
                        System.exit(1);
                    }
                }, (String[] strings) -> {
                    String firstArgument = strings[1];
                    if (firstArgument.contains("(")) {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                    StringBuilder typesList = new StringBuilder();
                    int i = 0;
                    while ((i < strings.length) && !strings[i].contains("(")) {
                        i++;
                    }
                    if (i != 2) {
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
                    String[] newArguments = new String[]{strings[0], strings[1], typesList.toString()};
                    return newArguments;
                }),
                new Command("drop", 2, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    String tableName = arguments[1];
                    if (currentTableName.equals(tableName)) {
                        state.setCurrentTableName("");
                    }
                    try {
                        provider.removeTable(tableName);
                        System.out.println("dropped");
                    } catch (IllegalStateException e) {
                        System.out.println(tableName + " not exists");
                    } catch (IOException io) {
                        System.err.println(Utility.IO_MSG + io.getMessage());
                        System.exit(1);
                    }
                }),
                new Command("use", 2, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String tableName = arguments[1];
                    Table newCurrentTable = provider.getTable(tableName);
                    String currentTableName = state.getCurrentTableName();
                    if (newCurrentTable != null) {
                        if (!currentTableName.equals("")) {
                            Table currentTable = provider.getTable(currentTableName);
                            if (currentTable.getNumberOfUncommittedChanges() > 0) {
                                System.out.println(currentTable.getNumberOfUncommittedChanges()
                                        + " unsaved changes");
                                throw new IllegalStateException();
                            } else {
                                state.setCurrentTableName(newCurrentTable.getName());
                                System.out.println("using " + tableName);
                            }
                        } else {
                            state.setCurrentTableName(newCurrentTable.getName());
                            System.out.println("using " + tableName);
                        }
                    } else {
                        System.out.println(tableName + " not exists");
                        throw new IllegalStateException();
                    }
                }),
                new ArgsListCommand("show", 2, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    List<String> keySet = provider.getTableNames();
                    System.out.println("table_name row_count");
                    for (String tableName : keySet) {
                        System.out.print(tableName + " ");
                        System.out.println(provider.getTable(tableName).size());
                    }
                }, (String[] strings) -> {
                    if (strings.length == 2 && strings[1].equals("tables")) {
                        return strings;
                    } else {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                }),
                new ArgsListCommand("put", 3, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        String key = arguments[1];
                        String valueList = arguments[2];
                        Storeable oldValue = null;
                        try {
                            oldValue = currentTable.put(key, provider.deserialize(currentTable, valueList));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e.getMessage(), e);
                        }
                        if (oldValue == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite");
                            System.out.println(provider.serialize(currentTable, oldValue));
                        }
                    }
                }, (String[] strings) -> {
                    String firstArgument = strings[1];
                    if (firstArgument.contains("[")) {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                    StringBuilder typesList = new StringBuilder();
                    int i = 0;
                    while ((i < strings.length) && !strings[i].contains("[")) {
                        i++;
                    }
                    if (i != 2) {
                        throw new IllegalArgumentException(strings[0] + Utility.INVALID_ARGUMENTS);
                    }
                    while (i < strings.length - 1) {
                        typesList.append(strings[i]);
                        typesList.append(" ");
                        i++;
                    }
                    typesList.append(strings[i]);
                    String[] newArguments = new String[]{strings[0], strings[1], typesList.toString()};
                    return newArguments;

                }),
                new Command("get", 2, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        String key = arguments[1];
                        Storeable value = currentTable.get(key);
                        if (value == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("found");
                            System.out.println(provider.serialize(currentTable, value));
                        }
                    }
                }),
                new Command("remove", 2, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        String key = arguments[1];
                        Storeable value = currentTable.remove(key);
                        if (value == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("removed");
                        }
                    }
                }),
                new Command("list", 1, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        List<String> list = currentTable.list();
                        String joined = String.join(", ", list);
                        System.out.println(joined);
                    }
                }),
                new Command("size", 1, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        System.out.println(currentTable.size());
                    }
                }),
                new Command("commit", 1, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        try {
                            System.out.println(currentTable.commit());
                        } catch (IOException io) {
                            System.err.println(Utility.IO_MSG + io.getMessage());
                            System.exit(1);
                        }
                    }
                }),
                new Command("rollback", 1, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        System.out.println(currentTable.rollback());
                    }
                }),
                new Command("exit", 1, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    try {
                        ((TableHolder) state.getTableHolder()).close();
                    } catch (IOException io) {
                        System.err.println("Some i/o error occured " + io.getMessage());
                        System.exit(1);
                    }
                    System.exit(0);
                })
        }).run(args);
    }
}
