package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class MkdirCommand implements Command {
    @Override
    public void execute(String[] args) throws IOException {
        if (args.length < 2) return;
        if (args[1] == null) return;
        File directory = new File(Shell.currentPath + File.separator + args[1]);
        if (!directory.mkdirs()) return;
    }

    @Override
    public String toString() {
        return "mkdir";
    }
}
