package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap;

import java.util.Scanner;

public final class MultiFileHashMapModes {
    public static void interactive() throws Exception {
        try (Scanner input = new Scanner(System.in)) {
            String cmd;
            String[] cmdargs;
            MultiFileHashMapManager parserCmd = new MultiFileHashMapManager();
            while (true) {
                System.out.print("$ ");
                cmd = input.nextLine();
                cmdargs = cmd.split(";");
                for (int i = 0; i < cmdargs.length; i++) {
                    parserCmd.parse(cmdargs[i], false);
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
        MultiFileHashMapManager parserCmd = new MultiFileHashMapManager();
        for (int i = 0; i < cmdargs.length; i++) {
            parserCmd.parse(cmdargs[i], true);
        }
        System.exit(0);
    }

}
