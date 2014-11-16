package ru.fizteh.fivt.students.kolmakov_sergey.j_unit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure.*;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure.TableClass;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.interpreter.Interpreter;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.interpreter.Command;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public final class Main {

    public static void main(String[] args) { // Main.
        String rootDirectory = System.getProperty("fizteh.db.dir");
        if (rootDirectory == null) {
            System.out.println("You must specify DataBase directory via -Dfizteh.db.dir JVM parameter");
            System.exit(1);
        }
        run(new DataBaseState(new TableManagerFactory().create(rootDirectory)), args);
    }

    private static void run(DataBaseState state, String[] args) {
        Interpreter dbInterpreter = new Interpreter(state, new Command[] {
            new Command("show", 1, 1, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    if (args[0].equals("tables")) {
                        TableManager manager = (TableManager) state.getManager();
                        Set<String> tableNames = manager.getNames();
                        System.out.println("table_name row_count");
                        for (String name : tableNames) {
                            Table curTable = manager.getTable(name);
                            System.out.println(curTable.getName() + " " + curTable.size());
                        }
                    } else {
                        throw new RuntimeException(Interpreter.BAD_COMMAND + "show " + args[0]);
                    }
                }
            }),
            new Command("list", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    Table currentTable = state.getCurrentTable();
                    if (currentTable != null) {
                        System.out.println(String.join(", ", currentTable.list()));
                    } else {
                        System.out.println("no table");
                    }
                }
            }),
            new Command("create", 1, 1, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    TableProvider manager = state.getManager();
                    if (manager.createTable(args[0]) != null) {
                        System.out.println("created");
                    } else {
                        System.out.println(args[0] + " exists");
                    }
                }
            }),
            new Command("use", 1, 1, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    TableProvider manager = state.getManager();
                    Table newTable = manager.getTable(args[0]);
                    if (newTable == null) {
                        throw new IllegalArgumentException(args[0] + " not exists");
                    }
                    TableClass usedTable = (TableClass) state.getCurrentTable();
                    if (usedTable != null && (usedTable.getNumberOfChanges() > 0) && usedTable != newTable) {
                        throw new IllegalArgumentException(usedTable.getNumberOfChanges() + " unsaved changes");
                    } else {
                        state.setCurrentTable(newTable);
                        System.out.println("using " + args[0]);
                    }
                }
            }),
            new Command("put", 2, 2, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    Table currentTable = state.getCurrentTable();
                    if (currentTable != null) {
                        String oldValue = currentTable.put(args[0], args[1]);
                        if (oldValue != null) {
                            System.out.println("overwrite");
                            System.out.println(oldValue);
                        } else {
                            System.out.println("new");
                        }
                    } else {
                        System.out.println("no table");
                    }
                }
            }),
            new Command("get", 1, 1, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    Table currentTable = state.getCurrentTable();
                    if (currentTable != null) {
                        String value = currentTable.get(args[0]);
                        if (value != null) {
                            System.out.println("found");
                            System.out.println(value);
                        } else {
                            System.out.println("not found");
                        }
                    } else {
                        System.out.println("no table");
                    }
                }
            }),
            new Command("remove", 1, 1, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    Table currentTable = state.getCurrentTable();
                    if (currentTable != null) {
                        String removedValue = currentTable.remove(args[0]);
                        if (removedValue != null) {
                            System.out.println("removed");
                        } else {
                            System.out.println("not found");
                        }
                    } else {
                        System.out.println("no table");
                    }
                }
            }),
            new Command("drop", 1, 1, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    TableProvider manager = state.getManager();
                    Table currentTable = state.getCurrentTable();
                    if (currentTable != null && currentTable.getName().equals(args[0])) {
                        state.setCurrentTable(null);
                    }
                    try {
                        manager.removeTable(args[0]);
                        System.out.println("dropped");
                    } catch (IllegalStateException e) {
                        System.out.println(args[0] + " doesn't exist");
                    }
                }
            }),
            new Command("size", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    Table currentTable = state.getCurrentTable();
                    if (currentTable != null) {
                        System.out.println(currentTable.size());
                    } else {
                        System.out.println("no table");
                    }
                }
            }),
            new Command("commit", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    Table currentTable = state.getCurrentTable();
                    if (currentTable != null) {
                        System.out.println(currentTable.commit());
                    } else {
                        System.out.println("no table");
                    }
                }
            }),
            new Command("rollback", 0, 0, new BiConsumer<DataBaseState, String[]>() {
                @Override
                public void accept(DataBaseState state, String[] args) {
                    Table currentTable = state.getCurrentTable();
                    if (currentTable != null) {
                        System.out.println(currentTable.rollback());
                    } else {
                        System.out.println("no table");
                    }
                }
            })
        });

        dbInterpreter.setExitHandler(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                TableClass table = (TableClass) state.getCurrentTable();
                if (table != null && (table.getNumberOfChanges() > 0)) {
                    System.out.println(table.getNumberOfChanges() + " unsaved changes");
                    return false;
                }
                return true;
            }

        });

        try {
            System.exit(dbInterpreter.run(args));
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            } else {
                System.out.println("Unexpected error in function run");
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
