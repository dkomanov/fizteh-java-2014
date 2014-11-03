package ru.fizteh.fivt.students.Bulat_Galiev.multifilehashmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

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
            } while (!input.equals("exit"));
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
                FileMapFunctions.use(arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "create":
            if (arg.length == 2) {
                FileMapFunctions.create(arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "drop":
            if (arg.length == 2) {
                FileMapFunctions.drop(arg[1]);
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
                    if (mode) {
                        System.exit(-1);
                    }
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
        case "exit":
            if (arg.length == 1) {
                FileMapFunctions.exit(provider);
                System.exit(0);
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

        public static void use(final String arg1) throws IOException {
            if (!arg1.equals("")) {
                TableProvider.changeCurTable(arg1);
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        }

        public static void create(final String arg1) throws IOException {
            if (!arg1.equals("")) {
                TableProvider.createTable(arg1);
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        }

        public static void drop(final String arg1) throws IOException {
            if (!arg1.equals("")) {
                TableProvider.removeTable(arg1);
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        }

        public static void showTables() {
            TableProvider.showTables();
        }

        public static void put(final TableProvider provider, final String arg1,
                final String arg2) throws IOException {
            if ((!arg1.equals("")) && (!arg2.equals(""))) {
                if (provider.getDataBase() != null) {
                    provider.getDataBase().put(arg1, arg2);
                } else {
                    System.err.println("no table selected");
                }
            } else {
                throw new IllegalArgumentException("Null key or name.");
            }
        }

        public static void get(final TableProvider provider, final String arg1)
                throws IOException {
            if (!arg1.equals("")) {
                if (provider.getDataBase() != null) {
                    provider.getDataBase().get(arg1);
                } else {
                    System.err.println("no table selected");
                }
            } else {
                throw new IllegalArgumentException("Null key.");
            }
        }

        public static void remove(final TableProvider provider,
                final String arg1) throws IOException {
            if (!arg1.equals("")) {
                if (provider.getDataBase() != null) {
                    provider.getDataBase().remove(arg1);
                } else {
                    System.err.println("no table selected");
                }
            } else {
                throw new IllegalArgumentException("Null key.");
            }
        }

        public static void list(final TableProvider provider)
                throws IOException {
            if (provider.getDataBase() != null) {
                provider.getDataBase().list();
            } else {
                System.err.println("no table selected");
            }
        }

        public static void exit(final TableProvider provider)
                throws IOException {
            if (provider.getDataBase() != null) {
                provider.getDataBase().commit();
            }
        }

    }
}
