package ru.fizteh.fivt.students.VasilevKirill.proxy.Commands;

import ru.fizteh.fivt.students.VasilevKirill.proxy.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.proxy.Commands.shelldata.Status;

import java.io.IOException;

/**
 * Created by Kirill on 20.10.2014.
 */
public class ShowTablesCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("Wrong arguments");
        }
        status.getMultiMap().showTables();
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null) {
            return false;
        }
        return args.length == 2;
    }

    @Override
    public String toString() {
        return "show";
    }
}
