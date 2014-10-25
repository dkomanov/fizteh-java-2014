package ru.fizteh.fivt.students.Bulat_Galiev.multifilehashmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public final class Modesfilemap {
    private Modesfilemap() {
        // Disable instantiation to this class.
    }

    public static void interactiveMode(final TableProvider provider)
            throws IOException {
        String input = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        do {
            System.out.print("$ ");

            try {
                input = in.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            parser(provider, input, false);
        } while (!input.equals("exit"));
    }

    public static void batchMode(final TableProvider provider,
            final String[] input) throws IOException {
        StringBuilder cmd = new StringBuilder();
        for (String argument : input) {
            if (cmd.length() != 0) {
                cmd.append(' ');
            }
            cmd.append(argument);
        }
        String arg = cmd.toString();
        parser(provider, arg, true);
        Filemapfunctions.exit(provider);
    }

    public static void commandHandler(final TableProvider provider,
            final String[] arg, final boolean mode) throws IOException {
        boolean argproblem = false;
        switch (arg[0]) {
        case "use":
            if (arg.length == 2) {
                Filemapfunctions.use(arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "create":
            if (arg.length == 2) {
                Filemapfunctions.create(arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "drop":
            if (arg.length == 2) {
                Filemapfunctions.drop(arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "show":
            if (arg.length == 2) {
                if (arg[1].equals("tables")) {
                    Filemapfunctions.showTables();
                } else {
                    System.out.println("Please enter proper command");
                }
            } else {
                argproblem = true;
            }
            break;
        case "put":
            if (arg.length == 3) {
                Filemapfunctions.put(provider, arg[1], arg[2]);
            } else {
                argproblem = true;
            }
            break;
        case "get":
            if (arg.length == 2) {
                Filemapfunctions.get(provider, arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "remove":
            if (arg.length == 2) {
                Filemapfunctions.remove(provider, arg[1]);
            } else {
                argproblem = true;
            }
            break;
        case "list":
            if (arg.length == 1) {
                Filemapfunctions.list(provider);
            } else {
                argproblem = true;
            }
            break;
        case "exit":
            if (arg.length == 1) {
                Filemapfunctions.exit(provider);
                System.exit(0);
            } else {
                argproblem = true;
            }
            break;
        default:
            System.out.println("Please enter proper command");
            if (mode) {
                System.exit(-1);
            }
        }
        if (argproblem) {
            System.out.println("Wrong number of arguments");
        }
    }

    public static void parser(final TableProvider provider, final String input,
            final boolean mod) throws IOException {
        final StringTokenizer tok = new StringTokenizer(input, ";", false);
        while (tok.hasMoreTokens()) {
            int i = 0;
            StringTokenizer argtok = new StringTokenizer(tok.nextToken(), " ",
                    false);
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

    public static final class Filemapfunctions {
        private Filemapfunctions() {
            // Disable instantiation to this class.
        }

        public static void use(final String arg1) throws IOException {
            if (!arg1.equals("")) {
                TableProvider.changeCurTable(arg1);
            } else {
                System.out.println("incorrect arguments");
                return;
            }
        }

        public static void create(final String arg1) throws IOException {
            if (!arg1.equals("")) {
                TableProvider.createTable(arg1);
            } else {
                System.out.println("incorrect arguments");
                return;
            }
        }

        public static void drop(final String arg1) throws IOException {
            if (!arg1.equals("")) {
                TableProvider.removeTable(arg1);
            } else {
                System.out.println("incorrect arguments");
                return;
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
                    System.out.println("no table selected");
                }
            } else {
                System.out.println("incorrect arguments");
                return;
            }
        }

        public static void get(final TableProvider provider, final String arg1)
                throws IOException {
            if (!arg1.equals("")) {
                if (provider.getDataBase() != null) {
                    provider.getDataBase().get(arg1);
                } else {
                    System.out.println("no table selected");
                }
            } else {
                System.out.println("incorrect arguments");
                return;
            }
        }

        public static void remove(final TableProvider provider,
                final String arg1) throws IOException {
            if (!arg1.equals("")) {
                if (provider.getDataBase() != null) {
                    provider.getDataBase().remove(arg1);
                } else {
                    System.out.println("no table selected");
                }
            } else {
                System.out.println("incorrect arguments");
                return;
            }
        }

        public static void list(final TableProvider provider)
                throws IOException {
            if (provider.getDataBase() != null) {
                provider.getDataBase().list();
            } else {
                System.out.println("no table selected");
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
