package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProviderFactory;

import java.io.IOException;

/**
 * Created by akhtyamovpavel on 07.12.14.
 */
public class ClientRun {
    public static void main(String[] args) {
        RemoteTableProviderFactory factory = new RemoteDataBaseTableProviderFactory();
        try {
            RemoteTableProvider provider = factory.connect("localhost", 10001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
