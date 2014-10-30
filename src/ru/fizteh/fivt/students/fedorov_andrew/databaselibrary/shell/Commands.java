package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;

import java.util.Map;
import java.util.Map.Entry;

public class Commands extends SimpleCommandContainer<SingleDatabaseShellState> {
    public static final Command<SingleDatabaseShellState> COMMIT =
            new AbstractCommand(null, "saves all changes made from the last commit", 1) {
                @Override
                public void executeSafely(SingleDatabaseShellState state, String[] args)
                        throws DatabaseException, IllegalArgumentException {
                    int changes = state.getActiveDatabase().commit();
                    System.out.println(changes);
                }
            };
    public static final Command<SingleDatabaseShellState> ROLLBACK =
            new AbstractCommand(null, "cancels all changes made from the last commit", 1) {

                @Override
                public void executeSafely(SingleDatabaseShellState state, String[] args)
                        throws DatabaseException, IllegalArgumentException {
                    int changes = state.getActiveDatabase().rollback();
                    System.out.println(changes);
                }
            };
    public static final Command<SingleDatabaseShellState> SIZE =
            new AbstractCommand(null, "prints count of stored keys in current table", 1) {
                @Override
                public void executeSafely(SingleDatabaseShellState state, String[] args)
                        throws DatabaseException, IllegalArgumentException {
                    int size = state.getActiveDatabase().getActiveTable().size();
                    System.out.println(size);
                }
            };
    public static final Command<SingleDatabaseShellState> CREATE =
            new AbstractCommand("<tablename>", "creates a new table with the given name", 2) {
                @Override
                public void executeSafely(SingleDatabaseShellState state, String[] args)
                        throws DatabaseException {
                    boolean created = state.getActiveDatabase().createTable(args[1]);
                    if (created) {
                        System.out.println("created");
                    } else {
                        throw new DatabaseException("Table " + args[1] + " exists");
                    }
                }
            };
    public static final Command<SingleDatabaseShellState> DROP = new AbstractCommand(
            "<tablename>", "deletes table with the given name from file system", 2) {
        @Override
        public void executeSafely(SingleDatabaseShellState state, final String[] args)
                throws DatabaseException {
            state.getActiveDatabase().dropTable(args[1]);
            System.out.println("dropped");
        }
    };
    public static final Command<SingleDatabaseShellState> EXIT =
            new AbstractCommand(null, "saves all data to file system and stops interpretation", 1) {
                @Override
                public void executeSafely(SingleDatabaseShellState state, String[] args)
                        throws DatabaseException, IllegalArgumentException {
                    state.persist();
                    state.exit(0);
                }
            };
    public static final Command<SingleDatabaseShellState> GET =
            new AbstractCommand("<key>", "obtains value by the key", 2) {
                @Override
                public void executeSafely(SingleDatabaseShellState state, final String[] args)
                        throws DatabaseException {
                    String key = args[1];

                    String value = state.getActiveDatabase().getActiveTable().get(key);

                    if (value == null) {
                        throw new DatabaseException("not found");
                    } else {
                        System.out.println("found");
                        System.out.println(value);
                    }
                }
            };
    public static final Command<SingleDatabaseShellState> HELP = new AbstractCommand(
            null, "prints out description of state commands", 1, Integer.MAX_VALUE) {
        @Override
        public void execute(SingleDatabaseShellState state, String[] args) {
            Map<String, Command<SingleDatabaseShellState>> commands = state.getCommands();

            System.out.println(
                    "MultiFileHashMap is an utility that lets you work with simple database");

            System.out.println(
                    String.format(
                            "You can set database directory to work with using environment variable '%s'",
                            SingleDatabaseShellState.DB_DIRECTORY_PROPERTY_NAME));

            for (Entry<String, Command<SingleDatabaseShellState>> cmdEntry : commands.entrySet()) {
                String cmdName = cmdEntry.getKey();
                Command<SingleDatabaseShellState> command = cmdEntry.getValue();

                System.out.println(
                        String.format(
                                "\t%s%s\t%s",
                                cmdName,
                                command.getInvocation() == null
                                ? ""
                                : (' ' + command.getInvocation()),
                                command.getInfo()));

            }
        }

        @Override
        public void executeSafely(SingleDatabaseShellState state, String[] args)
                throws DatabaseException {
            // not used
        }
    };
    public static final Command<SingleDatabaseShellState> LIST =
            new AbstractCommand(null, "prints all keys stored in the map", 1) {
                @Override
                public void executeSafely(SingleDatabaseShellState state, String[] args)
                        throws DatabaseException {
                    java.util.List<String> keySet =
                            state.getActiveDatabase().getActiveTable().list();
                    StringBuilder sb = new StringBuilder();

                    boolean comma = false;

                    for (String key : keySet) {
                        sb.append(comma ? ", " : "").append(key);
                        comma = true;
                    }

                    System.out.println(sb);
                }
            };
    public static final Command<SingleDatabaseShellState> PUT =
            new AbstractCommand("<key> <value>", "assigns new value to the key", 3) {
                @Override
                public void executeSafely(SingleDatabaseShellState state, String[] args)
                        throws DatabaseException {
                    String key = args[1];
                    String value = args[2];

                    String oldValue = state.getActiveDatabase().getActiveTable().put(key, value);

                    if (oldValue == null) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                        System.out.println("old " + oldValue);
                    }
                }
            };
    public static final Command<SingleDatabaseShellState> REMOVE =
            new AbstractCommand("<key>", "removes value by the key", 2) {
                @Override
                public void executeSafely(SingleDatabaseShellState state, String[] args)
                        throws DatabaseException {
                    String key = args[1];
                    String oldValue = state.getActiveDatabase().getActiveTable().remove(key);

                    if (oldValue == null) {
                        throw new DatabaseException("not found");
                    } else {
                        System.out.println("removed");
                    }
                }
            };
    public static final Command<SingleDatabaseShellState> SHOW = new AbstractCommand(
            "tables", "prints info on all tables assigned to the working database", 2) {
        @Override
        public void executeSafely(SingleDatabaseShellState state, String[] args)
                throws DatabaseException, IllegalArgumentException {
            switch (args[1]) {
            case "tables": {
                state.getActiveDatabase().showTables();
                break;
            }
            default: {
                throw new IllegalArgumentException("show: unexpected option: " + args[1]);
            }
            }
        }
    };
    public static final Command<SingleDatabaseShellState> USE = new AbstractCommand(
            "<tablename>",
            "saves all changes made to the current table (if present) and makes table"
            + " with the given name the current one",
            2) {
        @Override
        public void executeSafely(final SingleDatabaseShellState state, final String[] args)
                throws DatabaseException {
            state.getActiveDatabase().useTable(args[1]);
            System.out.println("using " + args[1]);
        }
    };
    private static final Commands INSTANCE = new Commands();

    private Commands() {

    }

    public static final Commands obtainInstance() {
        return INSTANCE;
    }
}
