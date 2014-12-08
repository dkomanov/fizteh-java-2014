package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote;

import java.io.IOException;

/**
 * Created by akhtyamovpavel on 09.12.14.
 */
public class Closer extends Thread {

    RemoteDataBaseTableProvider provider;

    public Closer(RemoteDataBaseTableProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        while (provider.isServerStarted()) {
            try {
                provider.stopServer();
                if (provider.isGuested()) {
                    provider.disconnect();
                    provider.setGuested(false);
                }
            } catch (IOException e) {
                //skip throwing exceptions
            }
        }
    }
}
