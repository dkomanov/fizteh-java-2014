package ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.filemap;

import ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata.Status;

import java.io.IOException;

/**
 * Created by Kirill on 11.10.2014.
 */
public class GetCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException(this.toString() + ": Arguments are invalid");
        }
        String value = status.getFileMap().getMap().get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + value);
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
        return "get";
    }
}
