package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by user1 on 07.10.2014.
 */
public class MultiFileMapMain {
    public static void main(String[] args) {

        LoggerFactory loggerFactory = new LoggerFactory();
        HashMap map = new HashMap();


        Shell shell = new Shell();
        DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();



        String pathName = "/home/akhtyamovpavel/Development/test/test2/";
        DataBaseTableProvider provider = null;
        try {
            provider = new DataBaseTableProvider(pathName);
        } catch (Exception e) {
            System.out.println("failed");
        }
        RemoteDataBaseTableProvider remoteProvider = new RemoteDataBaseTableProvider(provider);
        shell.setProvider(remoteProvider);
        shell.startInteractiveMode();
    }
}
