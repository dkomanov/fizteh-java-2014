package ru.fizteh.fivt.students.LebedevAleksey.junit;

import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String USE_COMMAND = "use";
    public static final boolean NO_TABLE_RETURN_CODE = true;
    public static final String EXIT_COMMAND = "exit";

    public static void main(String[] args) {
        try {
            DatabaseState state = new DatabaseState();
            List<Command> commands = new ArrayList<>(MultiFileHashMap.getCommands());
            for (int i = 0; i < commands.size(); i++) {
                if (commands.get(i).getName().equals(USE_COMMAND)) {
                    commands.set(i, new Command(USE_COMMAND, 1) {
                        @Override
                        protected boolean action(InterpreterState state, String[] arguments) {
                            Table currentTable;
                            int changesCount = 0;
                            currentTable = getCurrentTableWithoutMessages(state);
                            if (currentTable != null) {
                                changesCount = currentTable.changesCount();
                            }
                            if (changesCount == 0) {
                                String name = arguments[0];
                                try {
                                    MultiFileHashMap.toDatabaseState(state).getDatabase().useTable(name);
                                    System.out.println("using " + name);
                                } catch (TableNotFoundException ex) {
                                    System.out.println(name + " not exists");
                                } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                                    System.err.println(e.getMessage());
                                    return false;
                                }
                            } else {
                                System.out.println(changesCount + " unsaved changes");
                            }
                            return true;
                        }
                    });
                }
                if (commands.get(i).getName().equals(EXIT_COMMAND)) {
                    commands.set(i, new Command(EXIT_COMMAND, 0) {
                        @Override
                        protected boolean action(InterpreterState state, String[] arguments) {
                            Table currentTable = getCurrentTableWithoutMessages(state);
                            int changesCount = 0;
                            if (currentTable != null) {
                                changesCount = currentTable.changesCount();
                            }
                            if (changesCount == 0) {
                                state.exit();
                                return false;
                            } else {
                                System.out.println(changesCount + " unsaved changes");
                            }
                            return true;
                        }
                    });
                }
            }
            commands.add(new Command("commit", 0) {
                @Override
                protected boolean action(InterpreterState state, String[] arguments) {
                    try {
                        System.out.println(getCurrentTable(state).commit());
                        return true;
                    } catch (DatabaseFileStructureException | LoadOrSaveException e) {
                        System.err.println(e.getMessage());
                        return false;
                    } catch (TableNotFoundException e) {
                        return true;
                    }
                }
            });
            commands.add(new Command("rollback", 0) {
                @Override
                protected boolean action(InterpreterState state, String[] arguments) {
                    try {
                        System.out.println(getCurrentTable(state).rollback());
                    } catch (TableNotFoundException e) {
                        return true;
                    }
                    return NO_TABLE_RETURN_CODE;
                }
            });
            commands.add(new Command("size", 0) {
                @Override
                protected boolean action(InterpreterState state, String[] arguments) {
                    try {
                        System.out.println(getCurrentTable(state).count());
                        return true;
                    } catch (LoadOrSaveException | DatabaseFileStructureException e) {
                        System.err.println(e.getMessage());
                        return false;
                    } catch (TableNotFoundException e) {
                        return NO_TABLE_RETURN_CODE;
                    }
                }
            });
            runDatabaseInterpreter(args, state, commands);
        } catch (DatabaseFileStructureException | LoadOrSaveException ex) {
            System.err.println(ex.getMessage());
            System.exit(4);
        }
    }

    private static Table getCurrentTableWithoutMessages(InterpreterState state) {
        return (Table) ((DatabaseState) state).getDatabase().getCurrentTable();
    }

    private static Table getCurrentTable(InterpreterState state) throws TableNotFoundException {
        return ((Table) MultiFileHashMap.getCurrentTable(state));
    }

    private static void runDatabaseInterpreter(String[] args, DatabaseState state, List<Command> commands) {
        Interpreter interpreter = new Interpreter(commands, state);
        interpreter.run(args);
        if (args.length != 0) {
            if (!state.tryToSave()) {
                System.exit(2);
            }
        }
    }
}
