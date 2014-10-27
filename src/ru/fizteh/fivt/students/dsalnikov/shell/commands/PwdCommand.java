package ru.fizteh.fivt.students.dsalnikov.shell.commands;

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
        System.out.println(link.getState().getState());
    }
}
