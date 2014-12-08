package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProviderFactory;

import java.io.IOException;

/**
 * Created by akhtyamovpavel on 07.12.14.
 */
public class ClientRun {
    public static void main(String[] args) {
        String pathName = "/home/akhtyamovpavel/Development/test/test/";
        DataBaseTableProvider provider = null;
        try {
            provider = new DataBaseTableProvider(pathName);
        } catch (Exception e) {
            System.out.println("failed");
        }
        RemoteDataBaseTableProvider remoteProvider = new RemoteDataBaseTableProvider(provider);
        Shell shell = new Shell();
        shell.setProvider(remoteProvider);
        shell.startInteractiveMode();
    }
}
