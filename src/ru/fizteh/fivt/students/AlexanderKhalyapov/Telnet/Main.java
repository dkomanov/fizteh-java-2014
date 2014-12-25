package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public final class Main {
    public static void main(final String[] args) {
        String dbDirPath = System.getProperty("fizteh.db.dir");
        if (dbDirPath == null) {
            System.err.println("You must specify fizteh.db.dir via -Dfizteh.db.dir JVM parameter");
            System.exit(1);
        }
        try (TableManagerFactory factory = new TableManagerFactory()) {
            DataBaseState state = new DataBaseState(factory.create(dbDirPath));
            initializeAndRunInterpreter(state, args);
        } catch (RuntimeException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void initializeAndRunInterpreter(DataBaseState dbState, String[] arguments) {
        Interpreter dbInterpreter = new Interpreter(dbState, new Command[] {
                new Command("put", 2, (state, args) -> {
                    TableProvider manager = ((DataBaseState) state).getManager();
                    Table link = ((DataBaseState) state).getUsedTable();
                    if (link != null) {
                        try {
                            Storeable oldValue = link.put(args[0], manager.deserialize(link, args[1]));
                            if (oldValue != null) {
                                System.out.println("overwrite");
                                System.out.println(manager.serialize(link, oldValue));
                            } else {
                                System.out.println("new");
                            }
                        } catch (ColumnFormatException | ParseException e) {
                            throw new StopLineInterpretationException("wrong type (" + e.getMessage() + ")");
                        }
                    } else {
                        throw new StopLineInterpretationException("no table");
                    }
                }),
                new Command("get", 1, (state, args) -> {
                    TableProvider manager = ((DataBaseState) state).getManager();
                    Table link = ((DataBaseState) state).getUsedTable();
                    if (link != null) {
                        Storeable value = link.get(args[0]);
                        if (value != null) {
                            System.out.println("found");
                            System.out.println(manager.serialize(link, value));
                        } else {
                            System.out.println("not found");
                        }
                    } else {
                        throw new StopLineInterpretationException("no table");
                    }
                }),
                new Command("remove", 1, (state, args) -> {
                    Table link = ((DataBaseState) state).getUsedTable();
                    if (link != null) {
                        Storeable removedValue = link.remove(args[0]);
                        if (removedValue != null) {
                            System.out.println("removed");
                        } else {
                            System.out.println("not found");
                        }
                    } else {
                        throw new StopLineInterpretationException("no table");
                    }
                }),
                new Command("list", 0, (state, args) -> {
                    Table link = ((DataBaseState) state).getUsedTable();
                    if (link != null) {
                        System.out.println(String.join(", ", link.list()));
                    } else {
                        throw new StopLineInterpretationException("no table");
                    }
                }),
                new Command("size", 0, (state, args) -> {
                    Table link = ((DataBaseState) state).getUsedTable();
                    if (link != null) {
                        System.out.println(link.size());
                    } else {
                        throw new StopLineInterpretationException("no table");
                    }
                }),
                new Command("commit", 0, (state, args) -> {
                    Table link = ((DataBaseState) state).getUsedTable();
                    if (link != null) {
                        try {
                            System.out.println(link.commit());
                        } catch (IOException e) {
                            throw new StopLineInterpretationException(e.getMessage());
                        }
                    } else {
                        throw new StopLineInterpretationException("no table");
                    }
                }),
                new Command("rollback", 0, (state, args) -> {
                    Table link = ((DataBaseState) state).getUsedTable();
                    if (link != null) {
                        System.out.println(link.rollback());
                    } else {
                        throw new StopLineInterpretationException("no table");
                    }
                }),
                new Command("create", 2, (state, args) -> {
                    String[] types = args[1].substring(1, args[1].length() - 1).split(" ");
                    List<Class<?>> typesList = new ArrayList<>();
                    for (String type : types) {
                        Class<?> typeClass = Helper.SUPPORTED_NAMES_TO_TYPES.get(type);
                        if (typeClass != null) {
                            typesList.add(typeClass);
                        } else {
                            throw new StopLineInterpretationException("wrong type (" + "unsupported type: '" + type
                                    + "')");
                        }
                    }
                    TableProvider manager = ((DataBaseState) state).getManager();
                    try {
                        if (manager.createTable(args[0], typesList) != null) {
                            System.out.println("created");
                        } else {
                            throw new StopLineInterpretationException(args[0] + " exists");
                        }
                    } catch (IOException e) {
                        throw new StopLineInterpretationException(e.getMessage());
                    }
                }), new Command("use", 1, (state, args) -> {
            DataBaseState dataBaseState = ((DataBaseState) state);
            TableProvider manager = dataBaseState.getManager();
            Table newTable = manager.getTable(args[0]);
            Table usedTable = dataBaseState.getUsedTable();
            if (newTable != null) {
                if (usedTable != null && usedTable.getNumberOfUncommittedChanges() > 0) {
                    System.out.println(usedTable.getNumberOfUncommittedChanges() + " unsaved changes");
                } else {
                    dataBaseState.setUsedTable(newTable);
                    System.out.println("using " + args[0]);
                }
            } else {
                throw new StopLineInterpretationException(args[0] + " not exists");
            }
        }), new Command("drop", 1, (state, args) -> {
            DataBaseState dataBaseState = ((DataBaseState) state);
            TableProvider manager = dataBaseState.getManager();
            Table usedTable = dataBaseState.getUsedTable();
            if (usedTable != null && usedTable.getName().equals(args[0])) {
                dataBaseState.setUsedTable(null);
            }
            try {
                manager.removeTable(args[0]);
                System.out.println("dropped");
            } catch (IllegalStateException e) {
                throw new StopLineInterpretationException("tablename not exists");
            } catch (IOException e) {
                throw new StopLineInterpretationException(e.getMessage());
            }
        }), new Command("show", 1, (state, args) -> {
            if (args[0].equals("tables")) {
                TableProvider manager = ((DataBaseState) state).getManager();
                List<String> tableNames = manager.getTableNames();
                System.out.println("table_name row_count");
                for (String name : tableNames) {
                    Table curTable = manager.getTable(name);
                    System.out.println(curTable.getName() + " " + curTable.size());
                }
            } else {
                throw new StopLineInterpretationException(Interpreter.NO_SUCH_COMMAND_MSG + "show " + args[0]);
            }
        })});
        dbInterpreter.setExitHandler(() -> {
            DbTable link = (DbTable) dbState.getUsedTable();
            if (link != null && (link.getNumberOfUncommittedChanges() > 0)) {
                System.out.println(link.getNumberOfUncommittedChanges() + " unsaved changes");
                return false;
            }
            return true;
        });
        runInterpreter(dbInterpreter, arguments);
    }

    private static void runInterpreter(Interpreter interpreter, String[] args) {
        try {
            System.exit(interpreter.run(args));
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            } else {
                System.err.println("Something went wrong. Unexpected error");
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
