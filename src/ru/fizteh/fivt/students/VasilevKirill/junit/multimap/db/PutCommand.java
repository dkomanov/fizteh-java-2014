package ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db;

import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Status;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Kirill on 11.10.2014.
 */
public class PutCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (status.getFileMap().getMap() == null || !(checkArgs(args))) {
            throw new IOException(this.toString() + ": Arguments are invalid");
        }
        String oldValue = status.getFileMap().getMap().get(args[1]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + oldValue);
        }
        Map<String, String> newMap = status.getFileMap().getMap();
        newMap.put(args[1], args[2]);
        status.getFileMap().setMap(newMap);
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
