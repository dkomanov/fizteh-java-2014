package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;

/**
 * Created by Kirill on 07.12.2014.
 */
public class HandleCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        RemoteTableProvider tableProvider = status.getTableProvider();
        if (tableProvider == null) {
            throw new IOException("Can't find the table provider");
        }
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        return true;
    }

    @Override
    public String toString() {
        return "";
    }
}
