package ru.fizteh.fivt.students.VasilevKirill.parallel.Commands;

import ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata.Status;

import java.io.IOException;

/**
 * Created by Kirill on 19.10.2014.
 */
public class HandleTable implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (args == null) {
            throw new IOException("Wrong arguments");
        }
        if (!status.getMultiMap().handleTable(args)) {
            System.out.println("no table");
        }

        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        return false;
    }

    @Override
    public String toString() {
        return "";
    }
}
