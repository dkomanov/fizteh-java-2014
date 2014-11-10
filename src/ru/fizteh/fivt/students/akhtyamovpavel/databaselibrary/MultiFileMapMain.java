package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary;

import java.nio.file.Paths;

/**
 * Created by user1 on 07.10.2014.
 */
public class MultiFileMapMain {
    public static void main(String[] args) {
        DataBaseTableProviderFactory factory = null;
        DataBaseTableProvider shell = null;
        try {
            factory = new DataBaseTableProviderFactory();
            shell = factory.create(Paths.get(System.getProperty("user.dir"))
                    .resolve(System.getProperty("fizteh.db.dir")).toString());
        } catch (Exception e) {
            if (args.length == 0) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
        }
        if (args.length == 0) {
            shell.startInteractiveMode();
        } else {
            shell.startPacketMode(args);
        }
    }
}
