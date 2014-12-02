package ru.fizteh.fivt.students.ilivanov.FileMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(final String[] args) throws Exception {
        String file = System.getProperty("db.file");

        if (file == null) {
            System.err.println("file isn't specified");
            System.exit(-1);
        }

        ShellBigBoss database = null;
        try {
            database = new ShellBigBoss(file);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        Shell shell = new Shell();
        database.integrate(shell);

        int exitCode;
        if (args.length != 0) {
            exitCode = shell.runArgs(args);
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            exitCode = shell.run(br);
        }

        System.exit(exitCode);
    }
}
