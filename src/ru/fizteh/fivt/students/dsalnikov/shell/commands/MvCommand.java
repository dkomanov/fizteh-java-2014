package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;


public class MvCommand implements Command {

    private Shell link;

    public MvCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "mv";
    }

    public int getArgsCount() {
        return 2;
    }

    public void execute(String[] st) throws Exception {
        String[] rmstr = new String[3];
        String[] cpstr = new String[4];
        cpstr[1] = "-r";
        cpstr[2] = st[1];
        cpstr[3] = st[2];
        rmstr[2] = st[1];
        rmstr[1] = "-r";
        CpCommand cp = new CpCommand(link);
        RmCommand rm = new RmCommand(link);
        cp.execute(cpstr);
        rm.execute(rmstr);
    }
}
