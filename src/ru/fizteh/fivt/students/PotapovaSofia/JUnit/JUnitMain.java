package ru.fizteh.fivt.students.PotapovaSofia.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.PotapovaSofia.JUnit.Interpreter.Command;
import ru.fizteh.fivt.students.PotapovaSofia.JUnit.Interpreter.Interpreter;

import java.util.Set;
import java.util.function.BiConsumer;

public class JUnitMain {
    public static void main(String[] args) throws Exception {
        String pathName = System.getProperty("fizteh.db.dir");
        if (pathName == null) {
            System.err.println("You must specify db.file via -Ddb.file JVM parameter");
            System.exit(1);
        }
        TableProviderFactory factory = new DbTableProviderFactory();
        TableState state = new TableState(factory.create(pathName));

        Interpreter dbInterpreter = new Interpreter(state, new Command[]{
                new Command("put", 2, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
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
                new Command("get", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
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
                new Command("remove", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
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
                new Command("list", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            System.out.println(String.join(", ", currentTable.list()));
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("size", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            System.out.println(currentTable.size());
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("commit", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            int commitCount = 0;
                            //commitCount = currentTable.commit();
                            System.out.println(commitCount);
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("rollback", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            System.out.println(currentTable.rollback());
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("create", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        TableProvider tableProvider = state.getTableProvider();
                        if (tableProvider.createTable(args[0]) != null) {
                            System.out.println("created");
                        } else {
                            System.out.println(args[0] + " exists");
                        }
                    }
                }),
                new Command("use", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        TableProvider tableProvider = state.getTableProvider();
                        Table newTable = tableProvider.getTable(args[0]);
                        DbTable usedTable = (DbTable) state.getUsedTable();
                        if (newTable != null) {
                            if (usedTable != null && (usedTable.getDiffCount() > 0) && (usedTable != newTable)) {
                                //throw new IllegalArgumentException(usedTable.getDiffCount() + " unsaved changes");
                                System.out.println(usedTable.getDiffCount() + " unsaved changes");
                            } else {
                                state.setUsedTable(newTable);
                                System.out.println("using " + args[0]);
                            }
                        } else {
                            System.out.println(args[0] + " not exists");
                        }
                    }
                }),
                new Command("drop", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        TableProvider tableProvider = state.getTableProvider();
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null && currentTable.getName().equals(args[0])) {
                            state.setUsedTable(null);
                        }
                        try {
                            tableProvider.removeTable(args[0]);
                            System.out.println("dropped");
                        } catch (IllegalStateException e) {
                            System.out.println(args[0] + " not exists");
                        }
                    }
                }),
                new Command("show", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        if (args[0].equals("tables")) {
                            DbTableProvider tableProvider = (DbTableProvider) state.getTableProvider();
                            Set<String> tableNames = tableProvider.getTablesSet();
                            System.out.println("table_name row_count");
                            for (String name : tableNames) {
                                Table curTable = tableProvider.getTable(name);
                                System.out.println(curTable.getName() + " " + curTable.size());
                            }
                        } else {
                            System.out.println("Wrong command");
                        }
                    }
                }),
                new Command("exit", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                    }
                })
        });
        dbInterpreter.run(args);
    }
    public static void exit(TableState state) throws StopInterpretationException {
        TableProvider tableProvider = state.getTableProvider();
        DbTable usedTable = (DbTable) state.getUsedTable();
        if (usedTable != null && (usedTable.getDiffCount() > 0)) {
            System.out.println(usedTable.getDiffCount() + " unsaved changes");
        } else {
            throw new StopInterpretationException();
        }
    }
}
