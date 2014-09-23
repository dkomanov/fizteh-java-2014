package ru.fizteh.fivt.students.dsalnikov.shell.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

public class ExitCommand implements Command {

    private Shell link;

    public ExitCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "exit";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(String[] st) {
        if (st.length != 1) {
            throw new IllegalArgumentException("Incorrect usage of Command exit: wrong amount of arguments");
        } else {
            System.exit(0);
        }
    }
}
