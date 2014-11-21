package ru.fizteh.fivt.students.RadimZulkarneev.Main;

import java.util.function.BiConsumer;


import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.TableProviderFactoryRealize;
import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.TableProviderRealize;
import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.TableRealize;
import ru.fizteh.fivt.students.RadimZulkarneev.Interpreter.Command;
import ru.fizteh.fivt.students.RadimZulkarneev.Interpreter.Interpreter;
import ru.fizteh.fivt.students.RadimZulkarneev.Interpreter.InterpreterState;


public class JUnitMain {

    public static void main(String[] args) {
        String pathName = System.getProperty("fizteh.db.dir");
        if (pathName == null) {
            System.err.println("You must specify fizteh.db.file");
            System.exit(1);
        }
        TableProviderFactory tableProviderFactory = new TableProviderFactoryRealize();
        DataBaseInterpreterState state = new DataBaseInterpreterState(tableProviderFactory.create(pathName));
        new Interpreter(state, new Command[]{
                new Command("put", 2, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DataBaseInterpreterState state = (DataBaseInterpreterState) interpreterState;
                        Table table = state.getUsedTable();
                        if (table == null) {
                            throw new IllegalArgumentException("no table");
                        } else {
                            String key = arguments[0];
                            String value = arguments[1];
                            String retValue = table.put(key, value);
                            if (retValue == null) {
                                System.out.println("new");
                            } else {
                                System.out.println("overwrite");
                                System.out.println(retValue);
                            }
                        }
                    }
                }),
                new Command("get", 1, new BiConsumer<InterpreterState, String[]>() {

                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        Table table = ((DataBaseInterpreterState) interpreterState).getUsedTable();
                        if (table == null) {
                            throw new IllegalArgumentException("no table");
                        } else {
                            String value = table.get(arguments[0]);
                            if (value == null) {
                                System.out.println("not found");
                            } else {
                                System.out.println("found");
                                System.out.println(value);
                            }
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        Table table = ((DataBaseInterpreterState) interpreterState).getUsedTable();
                        if (table == null) {
                            throw new IllegalArgumentException("no table");
                        } else {
                            String retValue = table.remove(arguments[0]);
                            if (retValue == null) {
                                System.out.println("not found");
                            } else {
                                System.out.println("removed");
                            }
                        }
                    }
                }),
                new Command("list", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        Table table = ((DataBaseInterpreterState) interpreterState).getUsedTable();
                        if (table == null) {
                            throw new IllegalArgumentException("no table");
                        } else {
                            System.out.println(String.join(", ", table.list()));
                        }
                    }
                }),
                new Command("create", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        TableProvider tableProvider = ((DataBaseInterpreterState) interpreterState).getTableProvider();
                        if (tableProvider.createTable(arguments[0]) != null) {
                            System.out.println("created");
                        } else {
                            throw new IllegalArgumentException("tablename already exists");
                        }
                    }

                }),
                new Command("drop", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DataBaseInterpreterState dataBaseInterpreterState = (DataBaseInterpreterState) interpreterState;
                        TableProvider tableProvider = dataBaseInterpreterState.getTableProvider();
                        if (dataBaseInterpreterState.getUsedTable() != null
                                && dataBaseInterpreterState.getUsedTable().getName().equals(arguments[0])) {
                            dataBaseInterpreterState.setUsedTable(null);
                        }
                        try {
                            tableProvider.removeTable(arguments[0]);
                            System.out.println("removed");
                        } catch (IllegalStateException e) {
                            throw new IllegalArgumentException("tablename not exists");
                        }
                    }
                }),
                new Command("use", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DataBaseInterpreterState dataBaseInterpreterState =
                                (DataBaseInterpreterState) interpreterState;
                        TableProvider tableProvider = dataBaseInterpreterState.getTableProvider();
                        TableRealize previousTable = (TableRealize) dataBaseInterpreterState.getUsedTable();
                        Table nextTable = tableProvider.getTable(arguments[0]);
                        if (nextTable == null) {
                            throw new IllegalArgumentException(arguments[0] + " not exists");
                        } else {
                            if (previousTable != null && previousTable.getUncommitedChanges() > 0) {
                                throw new IllegalArgumentException(previousTable.getUncommitedChanges()
                                        + " unsaved changes");
                            }
                        }
                        System.out.println("using " + arguments[0]);
                        dataBaseInterpreterState.setUsedTable(nextTable);
                    }
                }),
                new Command("commit", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        Table table = ((DataBaseInterpreterState) interpreterState).getUsedTable();
                        if (table == null) {
                            throw new IllegalArgumentException("no table");
                        } else {
                            System.out.println(table.commit());
                        }
                    }
                }),
                new Command("show", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        if (arguments[0].equals("tables")) {
                            TableProviderRealize tableProvider = (TableProviderRealize) (((DataBaseInterpreterState)
                                    interpreterState).getTableProvider());
                            System.out.println(String.join("\n", tableProvider.getTableSet()));
                        } else {
                            throw new IllegalArgumentException("command not found: show " + arguments[0]);
                        }
                    }
                }),
                new Command("rollback", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        Table table = ((DataBaseInterpreterState) interpreterState).getUsedTable();
                        if (table != null) {
                            System.out.println(table.rollback());
                        } else {
                            throw new IllegalArgumentException("no table");
                        }
                    }
                })
        }, System.in, System.out).run(args);
    }
}

