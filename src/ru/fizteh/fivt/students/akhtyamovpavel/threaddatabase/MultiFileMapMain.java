package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase;

import java.nio.file.Paths;

/**
 * Created by user1 on 07.10.2014.
 */
public class MultiFileMapMain {
    public static void main(String[] args) {

        Shell shell = new Shell();
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        shell.setProvider(factory, Paths.get(System.getProperty("user.dir"))
                .resolve(System.getProperty("fizteh.db.dir")).toString());
        if (args.length == 0) {
            shell.startInteractiveMode();
        } else {
            shell.startPacketMode(args);
        }
    }
}
