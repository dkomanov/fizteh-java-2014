package ru.fizteh.fivt.students.VasilevKirill.telnet.structures;

import ru.fizteh.fivt.storage.structured.TableProvider;

/**
 * Created by Kirill on 08.12.2014.
 */
public class ShutdownHookThread extends Thread {
    private TableProvider tableProvider;

    public ShutdownHookThread() {

    }

    public ShutdownHookThread(TableProvider tableProvider) {
        this.tableProvider = tableProvider;
    }

    @Override
    public void run() {
        if (tableProvider != null) {
            try {
                ((MyTableProvider) tableProvider).close();
            } catch (Exception e) {
                System.err.println("But there was an error: " + e.getMessage());
            }
        }
    }

    public void setTableProvider(TableProvider tableProvider) {
        this.tableProvider = tableProvider;
    }
}
