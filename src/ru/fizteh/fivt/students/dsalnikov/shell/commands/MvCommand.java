package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.InputStream;
import java.io.PrintStream;


public class MvCommand extends AbstractCommand {

    private Shell link;

    public MvCommand(Shell s) {
        super("mv", 2);
        link = s;
    }

    public void execute(String[] st, InputStream inputStream, PrintStream outputStream) throws Exception {
        String[] rmstr = new String[3];
        String[] cpstr = new String[4];
        cpstr[1] = "-r";
        cpstr[2] = st[1];
        cpstr[3] = st[2];
        rmstr[2] = st[1];
        rmstr[1] = "-r";
        CpCommand cp = new CpCommand(link);
        RmCommand rm = new RmCommand(link);
        cp.execute(cpstr, inputStream, System.out);
        rm.execute(rmstr, inputStream, System.out);
    }
}
