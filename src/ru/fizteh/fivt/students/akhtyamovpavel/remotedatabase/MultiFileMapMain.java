package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.Closer;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.nio.file.Paths;

/**
 * Created by user1 on 07.10.2014.
 */
public class MultiFileMapMain {
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
        Shell shell = new Shell();
        RemoteDataBaseTableProvider remoteProvider = new RemoteDataBaseTableProvider(provider);
        Closer closer = new Closer(remoteProvider);
        Runtime.getRuntime().addShutdownHook(closer);
        shell.setProvider(remoteProvider);
        shell.startInteractiveMode();
    }
}
