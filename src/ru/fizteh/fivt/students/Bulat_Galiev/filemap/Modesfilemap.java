package ru.fizteh.fivt.students.Bulat_Galiev.filemap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public final class Modesfilemap {
    private Modesfilemap() {
        // Disable instantiation to this class.
    }

    public static void interactiveMode(final DatabaseSerializer link) {
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
            parser(link, input, false);
        } while (!input.equals("exit"));
    }

    public static void batchMode(final DatabaseSerializer link,
            final String[] input) {
        StringBuilder cmd = new StringBuilder();
        for (String argument : input) {
            if (cmd.length() != 0) {
                cmd.append(' ');
            }
            cmd.append(argument);
        }
        String arg = cmd.toString();
        parser(link, arg, true);
    }

    public static void commandHandler(final DatabaseSerializer link,
            final String[] arg, final boolean mode) {
        switch (arg[0]) {
        case "put":
            if (arg.length == 3) {
                Filemapfunctions.put(link, arg[1], arg[2]);
            } else {
                System.out.println("wrong number of arguments");
            }
            break;
        case "get":
            if (arg.length == 2) {
                Filemapfunctions.get(link, arg[1]);
            } else {
                System.out.println("wrong number of arguments");
            }
            break;
        case "remove":
            if (arg.length == 2) {
                Filemapfunctions.remove(link, arg[1]);
            } else {
                System.out.println("wrong number of arguments");
            }
            break;
        case "list":
            if (arg.length == 1) {
                Filemapfunctions.list(link);
            } else {
                System.out.println("wrong number of arguments");
            }
            break;
        case "exit":
            if (arg.length == 1) {
                Filemapfunctions.exit(link);
                System.exit(0);
            } else {
                System.out.println("wrong number of arguments");
            }
            break;

        default:
            System.out.println("Please enter proper command");
            if (mode) {
                System.exit(-1);
            }

        }
    }

    public static void parser(final DatabaseSerializer link,
            final String input, final boolean mod) {
        final StringTokenizer tok = new StringTokenizer(input, ";", false);
        while (tok.hasMoreTokens()) {
            int i = 0;
            StringTokenizer argtok = new StringTokenizer(tok.nextToken(), " ",
                    false);
            String[] arg = new String[argtok.countTokens()];
            while (argtok.hasMoreTokens()) {
                arg[i++] = argtok.nextToken();
            }
            commandHandler(link, arg, mod);
        }
        if (mod) {
            System.exit(0);
        }
    }

    public static final class Filemapfunctions {
        private Filemapfunctions() {
            // Disable instantiation to this class.
        }

        public static void put(final DatabaseSerializer link,
                final String arg1, final String arg2) {
            if ((!arg1.equals("")) && (!arg2.equals(""))) {
                Map<String, String> fileMap = link.getDataBase();
                String putValue = fileMap.put(arg1, arg2);
                if (putValue == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(putValue);
                }
            } else {
                System.out.println("incorrect arguments");
                return;
            }
        }

        public static void get(final DatabaseSerializer link, final String arg1) {
            if (!arg1.equals("")) {
                Map<String, String> fileMap = link.getDataBase();
                String getValue = fileMap.get(arg1);
                if (getValue == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(getValue);
                }
            } else {
                System.out.println("incorrect arguments");
                return;
            }
        }

        public static void remove(final DatabaseSerializer link,
                final String arg1) {
            if (!arg1.equals("")) {
                Map<String, String> fileMap = link.getDataBase();
                String getValue = fileMap.remove(arg1);
                if (getValue != null) {
                    System.out.println("removed");
                } else {
                    System.out.println("not found");
                }
            } else {
                System.out.println("incorrect arguments");
                return;
            }
        }

        public static void list(final DatabaseSerializer link) {
            Map<String, String> fileMap = link.getDataBase();
            Set<String> keys = fileMap.keySet();
            int iteration = 0;
            for (String current : keys) {
                iteration++;
                System.out.print(current);
                if (iteration != keys.size()) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }

        public static void exit(final DatabaseSerializer link) {
            link.close();
        }

    }
}
