package ru.fizteh.fivt.students.dnovikov.junit;

import javafx.util.Pair;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.TableNotFoundException;
import ru.fizteh.fivt.students.dnovikov.junit.Interpreter.Command;
import ru.fizteh.fivt.students.dnovikov.junit.Interpreter.Interpreter;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.function.BiConsumer;

public class MultiFileHashMapMain {

    private DataBaseProvider dbConnector = null;

    public static void main(String[] args) {
        MultiFileHashMapMain fileMap = new MultiFileHashMapMain();
        try {
            fileMap.run(args);
        } catch (LoadOrSaveException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void run(String[] args) throws IOException {
        String directoryPath = System.getProperty("fizteh.db.dir");
        if (directoryPath == null) {
            System.err.println("database directory not set");
            System.exit(1);
        }
        TableProviderFactory factory = new DataBaseProviderFactory();
        dbConnector = (DataBaseProvider) factory.create(directoryPath);
        Interpreter interpreter = new Interpreter(dbConnector, System.in, System.out, System.err, new Command[]{
                new Command("get", 1, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        String key = args[0];
                        if (dbConnector.getCurrentTable() == null) {
                            System.out.println("no table");
                        } else {
                            String result = dbConnector.getCurrentTable().get(key);
                            if (result == null) {
                                System.out.println("not found");
                            } else {
                                System.out.println("found");
                                System.out.println(result);
                            }
                        }
                    }
                }),
                new Command("put", 2, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        if (dbConnector.getCurrentTable() == null) {
                            System.out.println("no table");
                        } else {
                            String result = dbConnector.getCurrentTable().put(args[0], args[1]);
                            if (result == null) {
                                System.out.println("new");
                            } else {
                                System.out.println("overwrite");
                                System.out.println(result);
                            }
                        }
                    }
                }),
                new Command("list", 0, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        if (dbConnector.getCurrentTable() == null) {
                            System.out.println("no table");
                        } else {
                            List<String> result = dbConnector.getCurrentTable().list();
                            System.out.println(String.join(", ", result));
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        if (dbConnector.getCurrentTable() == null) {
                            System.out.println("no table");
                        } else {
                            String result = dbConnector.getCurrentTable().remove(args[0]);
                            if (result == null) {
                                System.out.println("not found");
                            } else {
                                System.out.println("removed");
                            }
                        }
                    }
                }),
                new Command("rollback", 0, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        if (dbConnector.getCurrentTable() == null) {
                            System.out.println("no table");
                        } else {
                            System.out.println(dbConnector.getCurrentTable().rollback());
                        }
                    }
                }),
                new Command("commit", 0, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        if (dbConnector.getCurrentTable() == null) {
                            System.out.println("no table");
                        } else {
                            System.out.println(dbConnector.getCurrentTable().commit());
                        }
                    }
                }),
                new Command("size", 0, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        if (dbConnector.getCurrentTable() == null) {
                            System.out.println("no table");
                        } else {
                            System.out.println(dbConnector.getCurrentTable().size());
                        }
                    }
                }),
                new Command("create", 1, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        try {
                            if (dbConnector.createTable(args[0]) == null) {
                                System.out.println(args[0] + " exists");
                            } else {
                                System.out.println("created");
                            }
                        } catch (LoadOrSaveException | InvalidPathException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }),
                new Command("show", 1, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        if (!args[0].equals("tables")) {
                            System.err.println("show" + ' ' + args[0] + ": no such command");
                        } else {
                            List<Pair<String, Integer>> result = dataBaseConnector.showTable();
                            for (Pair<String, Integer> table : result) {
                                System.out.println(table.getKey() + " " + table.getValue());
                            }
                        }
                    }
                }),
                new Command("use", 1, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        String name = new String(args[0]);
                        if (dbConnector.getCurrentTable() == null) {
                            if (dbConnector.getTable(name) != null) {
                                dbConnector.setCurrentTable(dbConnector.getTable(name));
                                System.out.println("using " + name);
                            } else {
                                System.out.println(name + " not exists");
                            }
                        } else {
                            int unsavedChanges = dbConnector.getCurrentTable().getNumberOfChanges();
                            if (dbConnector.getTable(name) != null) {
                                if (unsavedChanges > 0) {
                                    System.out.println(unsavedChanges + " unsaved changes");
                                } else {
                                    dbConnector.getCurrentTable().save();
                                    dbConnector.setCurrentTable(dbConnector.getTable(name));
                                    System.out.println("using " + name);
                                }
                            } else {
                                System.out.println(name + " not exists");

                            }
                        }
                    }
                }),
                new Command("drop", 1, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        try {
                            dbConnector.removeTable(args[0]);
                            System.out.println("dropped");
                        } catch (LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                        } catch (TableNotFoundException e) {
                            System.out.println(args[0] + " not exists");
                        }
                    }
                }),
                new Command("exit", 0, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        try {
                            if (dbConnector.getCurrentTable() != null) {
                                int unsavedChanges = dbConnector.getCurrentTable().getNumberOfChanges();
                                if (unsavedChanges > 0) {
                                    System.out.println("cannot exit: " + unsavedChanges + " unsaved changes");
                                } else {
                                    dbConnector.saveTable();
                                    System.exit(0);
                                }
                            } else {
                                dbConnector.saveTable();
                                System.exit(0);
                            }
                        } catch (LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                })
        });
        interpreter.run(args);
        if (interpreter.isBatch()) {
            if (dbConnector.getCurrentTable() != null) {
                int unsavedChanges = dbConnector.getCurrentTable().getNumberOfChanges();
                if (unsavedChanges > 0) {
                    System.out.println(unsavedChanges + " unsaved changes");
                } else {
                    dbConnector.saveTable();
                }
            }
        }
    }
}
