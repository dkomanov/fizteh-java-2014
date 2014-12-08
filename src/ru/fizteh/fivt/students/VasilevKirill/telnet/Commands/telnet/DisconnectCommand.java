package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;

/**
 * Created by Kirill on 07.12.2014.
 */
public class DisconnectCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("not disconnected: wrong arguments");
        }
        if (status.getTableProvider() == null) {
            throw new IOException("not connected");
        }
        //((MyRemoteTableProvider) status.getTableProvider()).disconnect();
        status.setTableProvider(null);
        System.out.println("disconnected");
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null || args.length != 1) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "disconnect";
    }
}
