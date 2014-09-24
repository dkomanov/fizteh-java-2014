package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class PwdCommand implements Command {
    @Override
    public void execute(String[] args) throws IOException {
        System.out.println(Shell.currentPath);
    }

    @Override
    public String toString() {
        return "pwd";
    }
}
