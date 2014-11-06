package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

import javafx.util.Pair;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap.Interpreter.*;


import javax.xml.crypto.Data;
import java.util.List;
import java.util.function.BiConsumer;

public class MultiFileHashMapMain {
    public static void main(String args[]) {
        TableProviderFactory databaseFactory = new DatabaseFactory();
        Database db = (Database) databaseFactory.create("fizteh.db.dir");
        DatabaseInterpreterState databaseInterpreterState
                = new DatabaseInterpreterState(db);
        new Interpreter(databaseInterpreterState, new Command[]{
                new Command("put", 2, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();
                        String key = arguments[0];
                        String value = arguments[1];

                        if (database.getUsingTable() == null) {
                            System.out.println("no table");
                        } else {
                            try {
                                String result = database.getUsingTable().put(arguments[0], arguments[1]);
                                if (result == null) {
                                    System.out.println("new");
                                } else {
                                    System.out.println("overwrite");
                                    System.out.println(result);
                                }
                            } catch (IllegalArgumentException e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    }
                }),
                new Command("list", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state
                                = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();
                        if (database.getUsingTable() == null) {
                            System.out.println("no table");
                        } else {
                            List<String> result = database.getUsingTable().list();
                            String joined = String.join(", ", result);
                            System.out.println(joined);
                        }
                    }
                }),
                new Command("get", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();
                        String key = arguments[0];

                        if (database.getUsingTable() == null) {
                            System.out.println("no table");
                        } else {
                            try {
                                String result = database.getUsingTable().get(arguments[0]);
                                if (result == null) {
                                    System.out.println("not found");
                                } else {
                                    System.out.println("found");
                                    System.out.println(result);
                                }
                            } catch (IllegalArgumentException e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();

                        if (database.getUsingTable() == null) {
                            System.out.println("no table");
                        } else {
                            try {
                                if (database.getUsingTable().remove(arguments[0]) != null) {
                                    System.out.println("removed");
                                } else {
                                    System.out.println("not found");
                                }
                            } catch (IllegalArgumentException e) {
                                System.err.println(e.getMessage());
                            } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    }
                }),
                new Command("use", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();

                        String name = arguments[0];
                        try {
                            database.useTable(name);
                            System.out.println("using " + name);
                        } catch (IllegalArgumentException e) {
                            System.out.println(name + " not exists");
                        } catch (TableNotFoundException e) {
                            System.out.println(name + " not exists");
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                        }
                    }

                }),
                new Command("drop", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();

                        String name = arguments[0];
                        try {
                            database.removeTable(name);
                            System.out.println("dropped");
                        } catch (TableNotFoundException ex) {
                            System.out.println(name + " not exists");
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }),
                new Command("create", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();

                        String name = arguments[0];
                        try {
                            database.createTable(name);
                            System.out.println("created");
                        } catch (TableAlreadyExistsException e) {
                            System.out.println(name + " exists");
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }),
                new Command("show", 1, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();

                        if (arguments[0].equals("tables")) {
                            try {
                                List<Pair<String, Integer>> tables = database.listTables();
                                for (Pair<String, Integer> table : tables) {
                                    System.out.println(table.getKey() + " " + table.getValue());
                                }
                            } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                                System.err.println(e.getMessage());
                            }
                        } else {
                            System.err.println("Unknown command.");
                        }
                    }

                }),
                new Command("commit", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();

                        if (database.getUsingTable() != null) {
                            int changes = database.getUsingTable().commit();
                            System.out.println("commited " + changes + " changes");
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("rollback", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();

                        if (database.getUsingTable() != null) {
                            int changes = database.getUsingTable().rollback();
                            System.out.println("rollback " + changes + " changes");
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("size", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        Database database = state.getDatabase();

                        if (database.getUsingTable() != null) {
                            int changes = database.getUsingTable().size();
                            System.out.println(changes);
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("exit", 0, new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] arguments) {
                        DatabaseInterpreterState state = (DatabaseInterpreterState) interpreterState;
                        state.tryToSave();
                        state.exit();
                    }
                })
        }).run(args);
    }
}

