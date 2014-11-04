package ru.fizteh.fivt.students.Bulat_Galiev.junit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public final class Modesfilemap {
    private static final int SLEEPNUMBER = 5;

    private Modesfilemap() {
        // Disable instantiation to this class.
    }

    public static void interactiveMode(final TableProvider provider)
            throws IOException {
        try {
            String input = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            do {
                try {
                    Thread.sleep(SLEEPNUMBER);
                } catch (InterruptedException e1) {
                    System.err.print(e1.getMessage());
                }
                System.out.print("$ ");
                try {
                    input = in.readLine();
                } catch (IOException e) {
                    System.err.print(e.getMessage());
                    System.exit(-1);
                }
                parser(provider, input, false);
            } while (true);
        } catch (NullPointerException e) {
            FileMapFunctions.exit(provider);
            System.exit(-1);
        }
    }

    public static void batchMode(final TableProvider provider,
            final String[] input) throws IOException {
        try {
            StringBuilder cmd = new StringBuilder();
            for (String argument : input) {
                if (cmd.length() != 0) {
                    cmd.append(' ');
                }
                cmd.append(argument);
            }
            String arg = cmd.toString();
            parser(provider, arg, true);
            FileMapFunctions.exit(provider);
        } catch (NullPointerException e) {
            FileMapFunctions.exit(provider);
            System.exit(-1);
        }
    }

    public static void commandHandler(final TableProvider provider,
            final String[] arg, final boolean mode) throws IOException {
        boolean argproblem = false;
        switch (arg[0]) {
        case "use":
            if (arg.length == 2) {
                FileMapFunctions.use(provider, arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "create":
            if (arg.length == 2) {
                FileMapFunctions.create(provider, arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "drop":
            if (arg.length == 2) {
                FileMapFunctions.drop(provider, arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "show":
            if (arg.length == 2) {
                if (arg[1].equals("tables")) {
                    FileMapFunctions.showTables();
                } else {
                    System.err.println(arg[0] + " " + arg[1]
                            + " is incorrect command");
                }
            } else {
                argproblem = true;
            }
            break;
        case "put":
            if (arg.length == 3) {
                FileMapFunctions.put(provider, arg[1], arg[2]);
            } else {
                argproblem = true;
            }
            break;
        case "get":
            if (arg.length == 2) {
                FileMapFunctions.get(provider, arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "remove":
            if (arg.length == 2) {
                FileMapFunctions.remove(provider, arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "list":
            if (arg.length == 1) {
                FileMapFunctions.list(provider);
            } else {
                argproblem = true;
            }
            break;
        case "commit":
            if (arg.length == 1) {
                FileMapFunctions.commit(provider);
            } else {
                argproblem = true;
            }
            break;
        case "size":
            if (arg.length == 1) {
                FileMapFunctions.size(provider);
            } else {
                argproblem = true;
            }
            break;
        case "rollback":
            if (arg.length == 1) {
                FileMapFunctions.rollback(provider);
            } else {
                argproblem = true;
            }
            break;
        case "exit":
            if (arg.length == 1) {
                FileMapFunctions.exit(provider);
                if (mode) {
                    System.exit(-1);
                }
            } else {
                argproblem = true;
            }
            break;
        default:
            System.err.println(arg[0] + " is incorrect command");
            if (mode) {
                System.exit(-1);
            }
        }
        if (argproblem) {
            System.err.println(arg[0] + ": wrong number of arguments");
            if (mode) {
                System.exit(-1);
            }
        }
    }

    public static void parser(final TableProvider provider, final String input,
            final boolean mod) throws IOException {
        final StringTokenizer tok = new StringTokenizer(input, ";", false);
        while (tok.hasMoreTokens()) {
            int i = 0;
            StringTokenizer argtok = new StringTokenizer(tok.nextToken(), " ",
                    false);
            if (argtok.countTokens() == 0) {
                System.err.println("null command");
                continue;
            }
            String[] arg = new String[argtok.countTokens()];
            while (argtok.hasMoreTokens()) {
                arg[i++] = argtok.nextToken();
            }

            commandHandler(provider, arg, mod);
        }
        if (mod) {
            System.exit(0);
        }
    }

    public static final class FileMapFunctions {
        private FileMapFunctions() {
            // Disable instantiation to this class.
        }

        public static void use(final TableProvider provider, final String arg1)
                throws IOException {
            TabledbProvider.changeCurTable(arg1);
        }

        public static void create(final TableProvider provider,
                final String arg1) throws IOException {
            provider.createTable(arg1);
        }

        public static void drop(final TableProvider provider, final String arg1)
                throws IOException {
            provider.removeTable(arg1);

        }

        public static void size(final TableProvider provider)
                throws IOException {
            Table curTable = ((TabledbProvider) provider).getDataBase();
            if (curTable != null) {
                System.out.println(curTable.size());
            } else {
                System.err.println("no table selected");
            }
        }

        public static void commit(final TableProvider provider)
                throws IOException {
            Table curTable = ((TabledbProvider) provider).getDataBase();
            if (curTable != null) {
                System.out.println(curTable.commit());
            } else {
                System.err.println("no table selected");
            }
        }

        public static void rollback(final TableProvider provider)
                throws IOException {
            Table curTable = ((TabledbProvider) provider).getDataBase();
            if (curTable != null) {
                curTable.rollback();
            } else {
                System.err.println("no table selected");
            }
        }

        public static void showTables() {
            TabledbProvider.showTables();
        }

        public static void put(final TableProvider provider, final String arg1,
                final String arg2) throws IOException {
            Table curTable = ((TabledbProvider) provider).getDataBase();
            if (curTable != null) {
                String putValue = curTable.put(arg1, arg2);
                if (putValue == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(putValue);
                }
            } else {
                System.err.println("no table selected");
            }

        }

        public static void get(final TableProvider provider, final String arg1)
                throws IOException {
            Table curTable = ((TabledbProvider) provider).getDataBase();
            if (curTable != null) {
                String getValue = curTable.get(arg1);
                if (getValue == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(getValue);
                }
            } else {
                System.err.println("no table selected");
            }
        }

        public static void remove(final TableProvider provider,
                final String arg1) throws IOException {
            Table curTable = ((TabledbProvider) provider).getDataBase();
            if (curTable != null) {
                String getValue = curTable.remove(arg1);
                if (getValue != null) {
                    System.out.println("removed");
                } else {
                    System.out.println("not found");
                }
            } else {
                System.err.println("no table selected");
            }
        }

        public static void list(final TableProvider provider)
                throws IOException {
            Table curTable = ((TabledbProvider) provider).getDataBase();
            if (curTable != null) {
                List<String> list = curTable.list();
                int iteration = 0;
                for (String current : list) {
                    iteration++;
                    System.out.print(current);
                    if (iteration != list.size()) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            } else {
                System.err.println("no table selected");
            }
        }

        public static void exit(final TableProvider provider)
                throws IOException {
            Table curTable = ((TabledbProvider) provider).getDataBase();
            if (curTable != null) {
                int diff = ((Tabledb) ((TabledbProvider) provider)
                        .getDataBase()).getdiffnrecords();
                if (diff == 0) {
                    curTable.commit();
                    System.exit(0);
                } else {
                    System.err
                            .println(diff
                                    + " unsaved changes. Do commit or rollback command before exit");
                }
            }
        }

    }
}
