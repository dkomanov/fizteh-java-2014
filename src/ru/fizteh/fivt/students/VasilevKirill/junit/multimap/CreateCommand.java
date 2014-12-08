package ru.fizteh.fivt.students.VasilevKirill.junit.multimap;

import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Status;

import java.io.IOException;

/**
 * Created by Kirill on 19.10.2014.
 */
public class CreateCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("Wrong arguments");
        }
        if (status.getMultiMap().createTable(args[1]) != null) {
            System.out.println("created");
        } else {
            System.out.println(args[1] + " exists");
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
        return "create";
    }
}
