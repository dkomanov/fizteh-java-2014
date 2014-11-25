package ru.fizteh.fivt.students.valentine_lebedeva.filemap;

import java.util.Scanner;

public final class Modes {
    public static void interactive() throws Exception {
        try (Scanner input = new Scanner(System.in)) {
            String cmd;
            String[] cmdargs;
            DB dataBase = new DB();
            while (true) {
                System.out.print("$ ");
                cmd = input.nextLine();
                cmdargs = cmd.split(";");
                for (int i = 0; i < cmdargs.length; i++) {
                    Parser parserCmd = new Parser();
                    parserCmd.parse(cmdargs[i], false, dataBase);
                }
            }
        }
    }

    public static void bath(final String[] args) throws Exception {
        String cmd;
        String[] cmdargs;
        StringBuilder builder = new StringBuilder();
        DB dataBase = new DB();
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            builder.append(" ");
        }
        cmd = builder.toString();
        cmdargs = cmd.split("; ");
        Parser parserCmd = new Parser();
        for (int i = 0; i < cmdargs.length; i++) {
            parserCmd.parse(cmdargs[i], true, dataBase);
        }
        dataBase.close();
        System.exit(0);
    }

    private Modes() {
        // Never called. Added only for checkstyle.
    }
}
