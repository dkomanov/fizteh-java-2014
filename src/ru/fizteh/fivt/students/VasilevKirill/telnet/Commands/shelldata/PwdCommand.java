package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata;

import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class PwdCommand implements Command {
    @Override
    public boolean checkArgs(String[] args) {
        return false;
    }

    @Override
    public int execute(String[] args, Status status) throws IOException {
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
