package ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.filemap;

import ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata.Status;

import java.io.IOException;
import java.util.Set;

/**
 * Created by Kirill on 11.10.2014.
 */
public class ListCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException(this.toString() + ": Argumnets are invalid");
        }
        Set<String> keySet = status.getFileMap().getMap().keySet();
        for (String key : keySet) {
            System.out.print(key + " ");
        }
        System.out.println();
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null) {
            return false;
        }
        return args.length == 1;
    }

    @Override
    public String toString() {
        return "list";
    }
}
