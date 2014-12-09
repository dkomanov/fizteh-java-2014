package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.NoSuchFileException;

public class CdCommand implements Command {

    private Shell link;

    public CdCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "cd";
    }

    public int getArgsCount() {
        return 1;
    }

    public void execute(String[] s, InputStream inputStream, PrintStream outputStream) throws IOException {
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
