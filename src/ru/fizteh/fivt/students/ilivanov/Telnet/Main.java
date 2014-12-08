package ru.fizteh.fivt.students.ilivanov.Telnet;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(final String[] args) throws Exception {
        String root = System.getProperty("fizteh.db.dir");

        if (root == null) {
            System.err.println("root directory isn't specified");
            System.exit(-1);
        }

        Shell shell = new Shell(System.out);
        try {
            FileMapProvider provider = new FileMapProviderFactory().create(root);
            ShellBigBoss database = new ShellBigBoss(provider);
            database.integrate(shell);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

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
