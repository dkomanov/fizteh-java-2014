package ru.fizteh.fivt.students.IvanShafran.shell;

import ru.fizteh.fivt.students.IvanShafran.shell.commands.*;

import java.io.File;
import java.util.HashMap;

public class Shell extends AbstractShell {
    private File workingDirectory;

    private void initWorkingDirectory() {
        workingDirectory = new File(System.getProperty("user.dir"));
    }

    private void initCommand() {
        command = new HashMap<String, Command>();

        command.put("pwd", new CommandPWD(this));
        command.put("exit", new CommandExit(this));
        command.put("mkdir", new CommandMKDIR(this));
        command.put("ls", new CommandLS(this));
        command.put("cd", new CommandCD(this));
        command.put("cat", new CommandCAT(this));
        command.put("cp", new CommandCP(this));
        command.put("rm", new CommandRM(this));
        command.put("mv", new CommandMV(this));
    }

    Shell() {
        initWorkingDirectory();
        initCommand();
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(File f) {
        workingDirectory = f;
    }

}
