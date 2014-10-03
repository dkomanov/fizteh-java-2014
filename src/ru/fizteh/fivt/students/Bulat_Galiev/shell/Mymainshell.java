package ru.fizteh.fivt.students.Bulat_Galiev.shell;

import java.io.*;
import java.util.StringTokenizer;

public final class Mymainshell {
    private Mymainshell() {
        // not called
    }
    public static void interactiveMode() {
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
            parser(input, false);
        } while (!input.equals("exit"));
    }

    public static void packageMode(final String[] input) {
        StringBuilder cmd = new StringBuilder();
        for (String argument : input) {
            cmd.append(argument);
            if (input.length > 1) {
                cmd.append(' ');
            }
        }
        String arg = cmd.toString();
        parser(arg, true);
    }

    public static void commandHandler(final String[] arg, final boolean mode) {
        switch (arg[0]) {
        case "cp":
            try {
                Shellfunc.cp(arg[1], arg[2], arg[3]);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // [-r]
            break;
        case "cd":
            Shellfunc.cd(arg[1]);
            break;
        case "mkdir":
            Shellfunc.mkdir(arg[1]);
            break;
        case "pwd":
            Shellfunc.pwd();
            break;
        case "rm":
            try {
                Shellfunc.rm(arg[1], arg[2]);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // [-r]
            break;
        case "mv":
            Shellfunc.mv(arg[1], arg[2]);
            break;
        case "ls":
            Shellfunc.ls();
            break;
        case "cat":
            try {
                Shellfunc.cat(arg[1]);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        case "exit":
            System.exit(-1);
            break;

        default:
            System.out.println("Please enter proper command");
            if (mode) {
                System.exit(-1);
            }

        }
    }

    public static void parser(final String input, final boolean mod) {
        final StringTokenizer tok = new StringTokenizer(input, ";", false);
        while (tok.hasMoreTokens()) {
            int i = 0;
            String[] arg = new String[4];
            arg[1] = "";
            arg[2] = "";
            arg[3] = "";
            StringTokenizer argtok = new StringTokenizer(tok.nextToken(), " ",
                    false);
            while (argtok.hasMoreTokens()) {
                arg[i++] = argtok.nextToken();
            }
            commandHandler(arg, mod);
        }
        if (mod) {
            System.exit(-1);
        }
    }
}
