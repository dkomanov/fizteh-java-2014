package ru.fizteh.fivt.students.valentine_lebedeva.shell;

import java.util.Scanner;

public final class Modes {

    public static void interactive() throws Exception {
        try (Scanner input = new Scanner(System.in)) {
            String cmd;
            String[] cmdargs;
            while (true) {
                System.out.print("$ ");

                cmd = input.nextLine();
                cmdargs = cmd.split(";");
                for (int i = 0; i < cmdargs.length; i++) {
                    Parser.parse(cmdargs[i], false);
                }
            }
        }
    }

    public static void bath(final String[] args) throws Exception {
            String cmd;
            String[] cmdargs;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                builder.append(args[i]);
                builder.append(" ");
            }
            cmd = builder.toString();
            cmdargs = cmd.split("; ");
            for (int i = 0; i < cmdargs.length; i++) {
                Parser.parse(cmdargs[i], true);
            }
            System.exit(0);
    }
    private Modes() {
    }
}
