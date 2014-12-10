package ru.fizteh.fivt.students.anastasia_ermolaeva.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.*;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.DatabaseIOException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.ExitException;

import java.util.List;
import java.util.Map;


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
        } catch (DatabaseIOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ExitException t) {
            System.exit(t.getStatus());
        }
    }

    public static void start(DBState tableState, final String[] args) throws ExitException {
        new Interpreter(tableState, new Command[]{
                new Command("create", 2, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String tableName = arguments[1];
                    if (provider.createTable(tableName) != null) {
                        System.out.println("created");
                    } else {
                        System.out.println(tableName + " exists");
                    }
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
                    }
                }),
                new Command("use", 2, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableHolder holder = (TableHolder) state.getTableHolder();
                    String tableName = arguments[1];
                    Table newCurrentTable = holder.getTable(tableName);
                    String currentTableName = state.getCurrentTableName();
                    if (newCurrentTable != null) {
                        if (!currentTableName.equals("")) {
                            DBTable currentTable = holder.getTableMap().get(currentTableName);
                            if (currentTable.getNumberOfChanges() > 0) {
                                System.out.println(currentTable.getNumberOfChanges()
                                        + " unsaved changes");
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
                    }
                }),
                new Command("show tables", 1, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableHolder holder = (TableHolder) state.getTableHolder();
                    Map<String, DBTable> tables = holder.getTableMap();
                    System.out.println("table_name row_count");
                    for (Map.Entry<String, DBTable> entry : tables.entrySet()) {
                        System.out.print(entry.getKey() + " ");
                        System.out.println(entry.getValue().size());
                    }
                }),
                new Command("put", 3, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        String key = arguments[1];
                        String value = arguments[2];
                        String oldValue = currentTable.put(key, value);
                        if (oldValue == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite");
                            System.out.println(oldValue);
                        }
                    }
                }),
                new Command("get", 2, (Object tableS, String[] arguments) -> {
                    DBState state = (DBState) tableS;
                    TableProvider provider = (TableProvider) state.getTableHolder();
                    String currentTableName = state.getCurrentTableName();
                    if (state.checkCurrentTable()) {
                        Table currentTable = provider.getTable(currentTableName);
                        String key = arguments[1];
                        String value = currentTable.get(key);
                        if (value == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("found");
                            System.out.println(value);
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
                        String value = currentTable.remove(key);
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
                        System.out.println(currentTable.commit());
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
                    ((TableHolder) state.getTableHolder()).close();
                    System.out.println("exit");
                    System.exit(0);
                })
        }).run(args);
    }
}
