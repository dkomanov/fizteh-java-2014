package ru.fizteh.fivt.students.dnovikov.junit;

import javafx.util.Pair;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.TableNotFoundException;
import ru.fizteh.fivt.students.dnovikov.junit.Interpreter.Command;
import ru.fizteh.fivt.students.dnovikov.junit.Interpreter.Interpreter;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.function.BiConsumer;

public class MultiFileHashMapMain {

    private DataBaseProvider dbConnector;

    public static void main(String[] args) {
        MultiFileHashMapMain fileMap = new MultiFileHashMapMain();
        String directoryPath = System.getProperty("fizteh.db.dir");
        if (directoryPath == null) {
            System.err.println("database directory not set");
            System.exit(1);
        }
        try {
            fileMap.run(args, directoryPath);
        } catch (LoadOrSaveException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void run(String[] args, String directoryPath) throws IOException {
        dbConnector = (DataBaseProvider) new DataBaseProviderFactory().create(directoryPath);
        Command[] commands = createCommands();
        Interpreter interpreter = new Interpreter(dbConnector, System.in, System.out, System.err, commands);
        interpreter.run(args);
        if (interpreter.isBatch()) {
            DataBaseTable currentTable = dbConnector.getCurrentTable();
            if (currentTable != null) {
                int unsavedChanges = currentTable.getNumberOfChanges();
                if (unsavedChanges > 0) {
                    System.out.println(unsavedChanges + " unsaved changes");
                } else {
                    dbConnector.saveTable();
                }
            }
        }
    }

    Command[] createCommands() {
        Command[] commands = new Command[]{
                new Command("get", 1, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        DataBaseTable currentTable = dbConnector.getCurrentTable();
                        if (currentTable == null) {
                            System.out.println("no table");
                        } else {
                            String result = currentTable.get(args[0]);
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
                        DataBaseTable currentTable = dbConnector.getCurrentTable();
                        if (currentTable == null) {
                            System.out.println("no table");
                        } else {
                            String result = currentTable.put(args[0], args[1]);
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
                        DataBaseTable currentTable = dbConnector.getCurrentTable();
                        if (currentTable == null) {
                            System.out.println("no table");
                        } else {
                            System.out.println(String.join(", ", currentTable.list()));
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        DataBaseTable currentTable = dbConnector.getCurrentTable();
                        if (currentTable == null) {
                            System.out.println("no table");
                        } else {
                            String result = currentTable.remove(args[0]);
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
                        DataBaseTable currentTable = dbConnector.getCurrentTable();
                        if (currentTable == null) {
                            System.out.println("no table");
                        } else {
                            System.out.println(currentTable.rollback());
                        }
                    }
                }),
                new Command("commit", 0, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        DataBaseTable currentTable = dbConnector.getCurrentTable();
                        if (currentTable == null) {
                            System.out.println("no table");
                        } else {
                            System.out.println(currentTable.commit());
                        }
                    }
                }),
                new Command("size", 0, new BiConsumer<DataBaseProvider, String[]>() {
                    @Override
                    public void accept(DataBaseProvider dataBaseConnector, String[] args) {
                        DataBaseTable currentTable = dbConnector.getCurrentTable();
                        if (currentTable == null) {
                            System.out.println("no table");
                        } else {
                            System.out.println(currentTable.size());
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
                        DataBaseTable currentTable = dbConnector.getCurrentTable();
                        if (currentTable == null) {
                            if (dbConnector.getTable(name) != null) {
                                dbConnector.setCurrentTable(dbConnector.getTable(name));
                                System.out.println("using " + name);
                            } else {
                                System.out.println(name + " not exists");
                            }
                        } else {
                            int unsavedChanges = currentTable.getNumberOfChanges();
                            if (dbConnector.getTable(name) != null) {
                                if (unsavedChanges > 0) {
                                    System.out.println(unsavedChanges + " unsaved changes");
                                } else {
                                    currentTable.save();
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
                            DataBaseTable currentTable = dbConnector.getCurrentTable();
                            if (currentTable != null) {
                                int unsavedChanges = currentTable.getNumberOfChanges();
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
                            System.exit(1);
                        }
                    }
                })};
        return commands;
    }
}

