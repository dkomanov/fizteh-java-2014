package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Kirill on 06.12.2014.
 */
public class StartCommand implements Command {
    @Override
    public String toString() {
        return "start";
    }

    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("not started: wrong port number");
        }
        ServerSocket ss = status.getServerSocket();
        if (ss != null) {
            throw new IOException("not started: already started");
        }
        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IOException("not started: wrong port number");
        }
        status.setServerSocket(new ServerSocket(port));
        System.out.println("started at " + port);
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null || args.length != 2) {
            return false;
        }
        return true;
    }
}
