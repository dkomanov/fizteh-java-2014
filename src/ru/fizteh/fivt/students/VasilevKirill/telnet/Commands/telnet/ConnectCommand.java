package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Kirill on 07.12.2014.
 */
public class ConnectCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("not connected: wrong arguments");
        }
        if (status.getSocket() != null) {
            throw new IOException("not connected: already connected");
        }
        int port = Integer.parseInt(args[2]);
        Socket socket = new Socket(args[1], port);
        status.setSocket(socket);
        System.out.println("connected");
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null || args.length != 3) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "connect";
    }
}
