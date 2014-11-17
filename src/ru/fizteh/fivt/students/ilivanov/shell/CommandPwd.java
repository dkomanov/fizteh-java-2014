package ru.fizteh.fivt.students.ilivanov.shell;

import java.util.ArrayList;

public class CommandPwd implements Command {

    CommandPwd(final ArrayList<String> parameters) throws Exception {
        if (parameters.size() != 1) {
            throw new Exception("wrong number of parameters");
        }
    }

    @Override
    public int execute() {
        try {
            System.out.println(Shell.currentDirectory);
        } catch (Exception e) {
            System.err.println("Current directory error");
            return -1;
        }

        return 0;
    }
}
