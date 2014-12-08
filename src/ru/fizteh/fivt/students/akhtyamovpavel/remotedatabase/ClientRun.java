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
        RemoteTableProviderFactory factory = new RemoteDataBaseTableProviderFactory();
        try {
            RemoteDataBaseTableProvider provider = (RemoteDataBaseTableProvider) factory.connect("localhost", 10001);
            String s = "create mama [int]";
            provider.sendCommand(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
