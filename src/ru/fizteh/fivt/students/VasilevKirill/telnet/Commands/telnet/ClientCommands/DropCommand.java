package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ClientCommands;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;

/**
 * Created by Kirill on 07.12.2014.
 */
public class DropCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("Wrong arguments");
        }
        try {
            status.getTableProvider().removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(args[1] + " not exists");
        }
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null || args[1] == null) {
            return false;
        }
        return args.length == 2;
    }

    @Override
    public String toString() {
        return "drop";
    }
}
