package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Kirill on 06.12.2014.
 */
public class StopCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("stop: wrong arguments");
        }
        ServerSocket ss = status.getServerSocket();
        if (ss == null) {
            throw new IOException("not started");
        }
        ss.close();
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null || args.length != 1) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "stop";
    }
}
