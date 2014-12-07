package ru.fizteh.fivt.students.VasilevKirill.db;

import ru.fizteh.fivt.students.VasilevKirill.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.db.shell.Status;

import java.io.IOException;

/**
 * Created by Kirill on 11.10.2014.
 */
public class PutCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (status.getFileMap().getMap() == null) {
            throw new IOException(this.toString() + ": Filemap wasn't initialized");
        }
        if (!(checkArgs(args))) {
            throw new IOException(this.toString() + ": Arguments are invalid");
        }
        String oldValue = status.getFileMap().getMap().get(args[1]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + oldValue);
        }
        status.getFileMap().getMap().put(args[1], args[2]);
        return 0;
    }

    @Override
    public String toString() {
        return "put";
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null) {
            return false;
        }
        return args.length == 3;
    }
}
