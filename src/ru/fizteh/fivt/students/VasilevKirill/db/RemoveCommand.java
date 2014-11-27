package ru.fizteh.fivt.students.VasilevKirill.db;

import ru.fizteh.fivt.students.VasilevKirill.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.db.shell.Status;

import java.io.IOException;

/**
 * Created by Kirill on 12.10.2014.
 */
public class RemoveCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (status.getFileMap().getMap() == null || !checkArgs(args)) {
            throw new IOException(this.toString() + ": Arguments are invalid");
        }
        String value = status.getFileMap().getMap().remove(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
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
        return "remove";
    }
}
