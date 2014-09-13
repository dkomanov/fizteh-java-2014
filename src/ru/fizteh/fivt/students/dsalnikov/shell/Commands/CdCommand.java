package ru.fizteh.fivt.students.dsalnikov.shell.Commands;

import ru.fizteh.fivt.students.dsalnikov.Utils.ShellState;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class CdCommand implements Command {
    public String getName() {
        return "cd";
    }

    public int getArgsCount() {
        return 1;
    }

    public void execute(String[] s) throws IOException {
        if (s.length != 2) {
            throw new IllegalArgumentException("Incorrect amount of arguments");
        } else {
            ShellState sh = link.getState();
            String currstate = sh.getState();
            String cdstate = s[1];
            File newdirectory = new File(s[1]);
            if (!newdirectory.isAbsolute()) {
                newdirectory = new File(sh.getState(), cdstate);
            }
            if (newdirectory.exists() && newdirectory.isDirectory()) {
                File curr = newdirectory.getCanonicalFile();
                sh.setState(curr.getAbsolutePath());
            } else {
                throw new NoSuchFileException("'" + cdstate + "' : No such file or directory");
            }
        }
    }

    public CdCommand(Shell s) {
        link = s;
    }

    private Shell link;
}
