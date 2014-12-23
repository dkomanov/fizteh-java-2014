package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Kirill on 07.12.2014.
 */
public class WhereamiCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("Whereami: wrong arguments");
        }
        Socket socket = status.getSocket();
        if (socket == null) {
            System.out.println("not connected");
        }
        InetAddress addr = socket.getInetAddress();
        if (addr.getCanonicalHostName().equals("127.0.0.1")) {
            System.out.println("local");
        } else {
            System.out.println(addr.getCanonicalHostName());
        }
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
        return "whereami";
    }
}
