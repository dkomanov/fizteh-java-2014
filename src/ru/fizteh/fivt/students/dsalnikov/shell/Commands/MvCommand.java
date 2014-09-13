package ru.fizteh.fivt.students.dsalnikov.shell.Commands;

import ru.fizteh.fivt.students.dsalnikov.Utils.StringUtils;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.File;
import java.nio.file.DirectoryNotEmptyException;

public class MvCommand implements Command {

    public String getName() {
        return "mv";
    }

    public int getArgsCount() {
        return 2;
    }

    public void execute(String[] st) throws Exception {
        if (st.length != 3 && st.length != 4) {
            throw new IllegalArgumentException("Incorrect usage of Command mv : wrong amount of arguments");
        } else if (st.length == 4) {
            String[] rmstr = new String[2];
            String[] cpstr = new String[3];
            cpstr[1] = st[2];
            cpstr[2] = st[3];
            rmstr[1] = st[2];
            CpCommand cp = new CpCommand(link);
            RmCommand rm = new RmCommand(link);
            cp.execute(st);
            rm.execute(rmstr);
        } else {
            File f = StringUtils.ProcessFile(link.getState().getState(), st[1]);
            if (f.isDirectory() && f.list().length != 0) {
                throw new DirectoryNotEmptyException("Source directory isn't empty. Use -r flag to move");
            }
        }
    }

    public MvCommand(Shell s) {
        link = s;
    }

    private Shell link;
}
