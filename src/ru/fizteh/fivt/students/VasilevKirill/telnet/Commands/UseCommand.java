package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;

/**
 * Created by Kirill on 19.10.2014.
 */
public class UseCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("Wrong arguments");
        }
        try {
            status.getMultiMap().setTable(args[1]);
            System.out.println("using " + args[1]);
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
        return "use";
    }
}
