package ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class MkdirCommand implements Command {
    @Override
    public boolean checkArgs(String[] args) {
        return false;
    }

    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (args.length != 2) {
            return 1;
        }
        File directory = new File(Shell.currentPath + File.separator + args[1]);
        if (!directory.mkdirs()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "mkdir";
    }
}
