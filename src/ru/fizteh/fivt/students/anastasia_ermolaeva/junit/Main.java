package ru.fizteh.fivt.students.anastasia_ermolaeva.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.Command;
//import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.ExitException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.ExitException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.Interpreter;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.TableState;

import java.util.*;

public class Main {

    public static void main(final String[] args) {
        String rootDirectory = System.getProperty("fizteh.db.dir");
        if (rootDirectory == null) {
            System.err.println("You must specify fizteh.db.dir via JVM parameter");
            System.exit(1);
        }
        TableProviderFactory factory = new TableHolderFactory();
        start(new TableState(factory.create(rootDirectory)), args);
    }

    public static void start(TableState tableState, final String[] args) {
        try {
            new Interpreter(tableState, new Command[] {
                    new Command("create", 2, (TableState tableS, String[] arguments) -> {
                        TableProvider holder = tableS.getTableHolder();
                        String tableName = arguments[1];
                        if (holder.createTable(tableName) != null) {
                            System.out.println("created");
                        } else {
                            System.out.println(tableName + " exists");
                        }
                    }),
                    new Command("drop", 2, (TableState tableS, String[] arguments) -> {
                        TableProvider holder = tableS.getTableHolder();
                        String currentTableName = tableS.getCurrentTableName();
                        String tableName = arguments[1];
                        if (currentTableName.equals(tableName)) {
                            tableS.setCurrentTableName("");
                        }
                        try {
                            holder.removeTable(tableName);
                            System.out.println("dropped");
                        } catch (IllegalStateException e) {
                            System.out.println(tableName + " not exists");
                        }
                    }),
                    new Command("use", 2, (TableState tableS, String[] arguments) -> {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        String tableName = arguments[1];
                        Table newCurrentTable = holder.getTable(tableName);
                        String currentTableName = tableS.getCurrentTableName();
                        if (newCurrentTable != null) {
                            if (!currentTableName.equals("")) {
                                DBTable currentTable = holder.getTableMap().get(currentTableName);
                                if (currentTable.getNumberOfChanges() > 0) {
                                    System.out.println(currentTable.getNumberOfChanges()
                                            + " unsaved changes");
                                } else {
                                    tableS.setCurrentTableName(newCurrentTable.getName());
                                    System.out.println("using " + tableName);
                                }
                            } else {
                                tableS.setCurrentTableName(newCurrentTable.getName());
                                System.out.println("using " + tableName);
                            }
                        } else {
                            System.out.println(tableName + " not exists");
                        }
                    }),
                    new Command("show tables", 1, (TableState tableS, String[] arguments) -> {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        Map<String, DBTable> tables = holder.getTableMap();
                        System.out.println("table_name row_count");
                        for (Map.Entry<String, DBTable> entry : tables.entrySet()) {
                            System.out.print(entry.getKey() + " ");
                            System.out.println(entry.getValue().size());
                        }
                    }),
                    new Command("put", 3, (TableState tableS, String[] arguments) -> {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        String currentTableName = tableS.getCurrentTableName();
                        if (currentTableName.equals("")) {
                            System.out.println("no table");
                        } else {
                            Table currentTable = holder.getTable(currentTableName);
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
                    new Command("get", 2, (TableState tableS, String[] arguments) -> {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        String currentTableName = tableS.getCurrentTableName();
                        if (currentTableName.equals("")) {
                            System.out.println("no table");
                        } else {
                            Table currentTable = holder.getTable(currentTableName);
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
                    new Command("remove", 2, (TableState tableS, String[] arguments) -> {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        String currentTableName = tableS.getCurrentTableName();
                        if (currentTableName.equals("")) {
                            System.out.println("no table");
                        } else {
                            Table currentTable = holder.getTable(currentTableName);
                            String key = arguments[1];
                            String value = currentTable.remove(key);
                            if (value == null) {
                                System.out.println("not found");
                            } else {
                                System.out.println("removed");
                            }
                        }
                    }),
                    new Command("list", 1, (TableState tableS, String[] arguments) -> {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        String currentTableName = tableS.getCurrentTableName();
                        if (currentTableName.equals("")) {
                            System.out.println("no table");
                        } else {
                            Table currentTable = holder.getTable(currentTableName);
                            List<String> list = currentTable.list();
                            String joined = String.join(", ", list);
                            System.out.println(joined);
                        }
                    }),
                    new Command("size", 1, (TableState tableS, String[] arguments) -> {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        String currentTableName = tableS.getCurrentTableName();
                        if (currentTableName.equals("")) {
                            System.out.println("no table");
                        } else {
                            Table currentTable = holder.getTable(currentTableName);
                            System.out.println(currentTable.size());
                        }
                    }),
                    new Command("commit", 1, (TableState tableS, String[] arguments) ->  {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        String currentTableName = tableS.getCurrentTableName();
                        if (currentTableName.equals("")) {
                            System.out.println("no table");
                        } else {
                            Table currentTable = holder.getTable(currentTableName);
                            System.out.println(currentTable.commit());
                        }
                    }),
                    new Command("rollback", 1, (TableState tableS, String[] arguments) ->  {
                        TableHolder holder = (TableHolder)tableS.getTableHolder();
                        String currentTableName = tableS.getCurrentTableName();
                        if (currentTableName.equals("")) {
                            System.out.println("no table");
                        } else {
                            Table currentTable = holder.getTable(currentTableName);
                            System.out.println(currentTable.rollback());
                        }
                    }),
                    new Command("exit", 1, (TableState tableS, String[] arguments) -> {
                        //try {
                            ((TableHolder)tableS.getTableHolder()).close();
                            System.out.println("exit");
                            System.exit(0);
                        //} catch (ExitException e) {
                          //  System.out.println("exit");
                           // System.exit(e.getStatus());
                        //}
                    })
            }).run(args);
        } catch (ExitException e) {
            ((TableHolder)tableState.getTableHolder()).close();
            System.exit(e.getStatus());
        }
    }
}
