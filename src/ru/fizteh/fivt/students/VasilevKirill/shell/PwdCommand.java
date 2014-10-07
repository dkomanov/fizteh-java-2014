package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class PwdCommand implements Command {
    @Override
    public int execute(String[] args) throws IOException {
        if (args.length != 1) {
            return 1;
        }
        System.out.println(Shell.currentPath);
        return 0;
    }

    @Override
    public String toString() {
        return "pwd";
    }
}
