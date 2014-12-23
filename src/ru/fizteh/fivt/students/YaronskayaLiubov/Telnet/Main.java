package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 21.12.14.
 */
public class Main {
    public static void main(String[] args) {
        StoreableDataTableProviderFactory factory = new StoreableDataTableProviderFactory();
        StoreableDataTableProvider provider = null;
        try {
            provider = (StoreableDataTableProvider) factory.create(System.getProperty("fizteh.db.dir"));
        } catch (IOException e) {
            System.err.println("error creating TableProvider: " + e.getMessage());
        }

        RemoteDataTableProvider remoteProvider = new RemoteDataTableProvider(provider);

        Shell shell = new Shell();
        shell.setProvider(remoteProvider);
        remoteProvider.setShell(shell);

        try {
            if (args.length == 0) {
                shell.interactiveMode();
            } else {
                shell.packetMode(args);
            }
        } catch (ShellRuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
