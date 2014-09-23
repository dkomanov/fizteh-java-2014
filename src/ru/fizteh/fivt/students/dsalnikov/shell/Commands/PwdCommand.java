package ru.fizteh.fivt.students.dsalnikov.shell.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

public class PwdCommand implements Command {

    private Shell link;

    public PwdCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "pwd";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(String[] emptyStr) {
        if (emptyStr.length != 1) {
            throw new IllegalArgumentException("Incorrect usage of command pwd: wrong amount of arguments");
        } else {
            System.out.println(link.getState().getState());
        }
    }
}
