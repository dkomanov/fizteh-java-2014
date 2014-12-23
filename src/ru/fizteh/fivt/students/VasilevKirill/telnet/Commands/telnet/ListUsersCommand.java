package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;
import ru.fizteh.fivt.students.VasilevKirill.telnet.ServerMain;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kirill on 14.12.2014.
 */
public class ListUsersCommand implements Command {
    @Override
    public boolean checkArgs(String[] args) {
        return !(args == null || args.length != 1);
    }

    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!args[1].equals("users")) {
            return 0;
        }
        List<String> users = ServerMain.getUserInformation();
        for (String it: users) {
            System.out.println(it);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "list";
    }
}
