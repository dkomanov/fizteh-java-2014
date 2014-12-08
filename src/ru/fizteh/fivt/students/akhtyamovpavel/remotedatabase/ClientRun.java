package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.Closer;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.nio.file.Paths;

/**
 * Created by akhtyamovpavel on 07.12.14.
 */
public class ClientRun {
    public static void main(String[] args) {
        String pathName = null;
        try {
            pathName = Paths.get(System.getProperty("user.dir"))
                    .resolve(System.getProperty("fizteh.db.dir")).toString();
        } catch (Exception e) {
            System.err.println("Enter db dir file");
            System.exit(-1);
        }
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
        DataBaseTableProvider provider = null;
        try {
            provider = factory.create(pathName);
        } catch (Exception e) {
            System.out.println("failed");
            System.exit(-1);
        }

        RemoteDataBaseTableProvider remoteProvider = new RemoteDataBaseTableProvider(provider);
        Closer closer = new Closer(remoteProvider);
        Runtime.getRuntime().addShutdownHook(closer);
        Shell shell = new Shell();
        shell.setProvider(remoteProvider);
        shell.startInteractiveMode();
    }
}
