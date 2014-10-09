package ru.fizteh.fivt.students.Bulat_Galiev.filemap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public final class Modesfilemap {
    private Modesfilemap() {
        // not called
    }

    public static void interactiveMode(final Data link) {
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

    public static void packageMode(final Data link, final String[] input) {
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

    public static void commandHandler(final Data link, final String[] arg,
            final boolean mode) {
        switch (arg[0]) {
        case "put":
            if (arg.length == 3) {
                Filemapfunc.put(link, arg[1], arg[2]);
            }
            break;
        case "get":
            if (arg.length == 2) {
                Filemapfunc.get(link, arg[1]);
            }
            break;
        case "remove":
            if (arg.length == 2) {
                Filemapfunc.remove(link, arg[1]);
            }
            break;
        case "list":
            if (arg.length == 1) {
                Filemapfunc.list(link);
            }
            break;
        case "exit":
            if (arg.length == 1) {
                Filemapfunc.exit(link);
                System.exit(0);
            }
            break;

        default:
            System.out.println("Please enter proper command");
            if (mode) {
                System.exit(-1);
            }

        }
    }

    public static void parser(final Data link, final String input, final boolean mod) {
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
}
