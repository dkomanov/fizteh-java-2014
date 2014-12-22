package ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db;

import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Status;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Kirill on 12.10.2014.
 */
public class RemoveCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (status.getFileMap().getMap() == null || !checkArgs(args)) {
            throw new IOException(this.toString() + ": Arguments are invalid");
        }
        Map<String, String> newMap = status.getFileMap().getMap();
        String value = newMap.remove(args[1]);
        status.getFileMap().setMap(newMap);
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
