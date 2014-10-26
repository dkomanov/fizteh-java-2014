package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import java.util.Arrays;
import java.util.List;

public class MultiFileHashMap {

    public MultiFileHashMap() {
    }


    public static void main(String[] args) {
        runInterpreter(args, getCommands());
    }

    public static void runInterpreter(String[] args, List<Command> commands) {
        try {
            DatabaseState state = new DatabaseState();
            Interpreter interpreter = new Interpreter(commands, state);
            interpreter.run(args);
            if (args.length != 0) {
                if (!state.tryToSave()) {
                    System.exit(2);
                }
            }
        } catch (DatabaseFileStructureException | LoadOrSaveException ex) {
            System.err.println(ex.getMessage());
            System.exit(3);
        }
    }

    public static List<Command> getCommands() {
        return Arrays.asList(
                new Command("exit", 0) {
                    @Override
                    public boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        if (toDatabaseState(state).tryToSave()) {
                            state.exit();
                        }
                        return false;
                    }
                },

                new Command("show", 1) {
                    @Override
                    public boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        if (arguments[0].equals("tables")) {
                            try {
                                List<Pair<String, Integer>> tables = toDatabaseState(state).getDatabase().listTables();
                                for (Pair<String, Integer> table : tables) {
                                    System.out.println(table.getKey() + " " + table.getValue());
                                }
                                return true;
                            } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                                System.err.println(e.getMessage());
                                return false;
                            }
                        } else {
                            throw new ArgumentException("Unknown argument in show command");
                        }
                    }
                },

                new Command("create", 1) {
                    @Override
                    protected boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        String name = arguments[0];
                        try {
                            toDatabaseState(state).getDatabase().createTable(name);
                            System.out.println("created");
                        } catch (TableAlreadyExistsException ex) {
                            System.out.println(name + " exists");
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                            return false;
                        }
                        return true;
                    }
                },

                new Command("drop", 1) {
                    @Override
                    protected boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        String name = arguments[0];
                        try {
                            toDatabaseState(state).getDatabase().removeTable(name);
                            System.out.println("dropped");
                        } catch (TableNotFoundException ex) {
                            System.out.println(name + " not exists");
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                            return false;
                        }
                        return true;
                    }
                },

                new Command("use", 1) {
                    @Override
                    protected boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        String name = arguments[0];
                        try {
                            toDatabaseState(state).getDatabase().useTable(name);
                            System.out.println("using " + name);
                        } catch (TableNotFoundException ex) {
                            System.out.println(name + " not exists");
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                            return false;
                        }
                        return true;
                    }
                },

                new Command("list", 0) {
                    @Override
                    protected boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        try {
                            List<String> result = getCurrentTable(state).list();
                            for (int i = 0; i < result.size(); i++) {
                                if (i > 0) {
                                    System.out.print(", ");
                                }
                                System.out.print(result.get(i));
                            }
                            System.out.println();
                            return true;
                        } catch (TableNotFoundException e) {
                            return true;
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                            return false;
                        }

                    }
                },

                new Command("put", 2) {
                    @Override
                    protected boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        try {
                            String result = getCurrentTable(state).put(arguments[0], arguments[1]);
                            if (result == null) {
                                System.out.println("new");
                            } else {
                                System.out.println("overwrite");
                                System.out.println(result);
                            }
                            return true;
                        } catch (TableNotFoundException e) {
                            return true;
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                            return false;
                        }

                    }
                },

                new Command("get", 1) {
                    @Override
                    protected boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        try {
                            String result = getCurrentTable(state).get(arguments[0]);
                            if (result == null) {
                                System.out.println("not found");
                            } else {
                                System.out.println("found");
                                System.out.println(result);
                            }
                            return true;
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                            return false;
                        } catch (TableNotFoundException e) {
                            return true;
                        }
                    }
                },

                new Command("remove", 1) {
                    @Override
                    protected boolean action(InterpreterState state, String[] arguments) throws ArgumentException {
                        try {
                            if (getCurrentTable(state).remove(arguments[0])) {
                                System.out.println("removed");
                            } else {
                                System.out.println("not found");
                            }
                            return true;
                        } catch (TableNotFoundException e) {
                            return true;
                        } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                            System.err.println(e.getMessage());
                            return false;
                        }
                    }
                }
        );
    }

    public static Table getCurrentTable(InterpreterState state) throws TableNotFoundException {
        Table table = toDatabaseState(state).getDatabase().getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            throw new TableNotFoundException("No table selected");
        } else {
            return table;
        }
    }

    public static DatabaseState toDatabaseState(InterpreterState state) {
        return (DatabaseState) state;
    }
}
