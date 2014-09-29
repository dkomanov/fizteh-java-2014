package ru.fizteh.fivt.students.akhtyamovpavel.shell;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.commands.*;

import java.io.File;
import java.util.HashMap;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class Shell extends AbstractShell {
    private File workDirectory;

    private void initWorkDirectory() {
        workDirectory = new File(System.getProperty("user.dir"));
    }

    private void initCommand() {
        commandNames = new HashMap<String, Command>();

        addCommand(new PrintWorkDirectoryCommand(this));
        addCommand(new DirectoryListCommand(this));
        addCommand(new ExitCommand());
        addCommand(new MakeDirectoryCommand(this));
    }

    Shell() {
        initWorkDirectory();
        initCommand();
    }

    public File getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(File f) {
        workDirectory = f;
    }

    public void addCommand(Command command) {
        commandNames.put(command.getName(), command);
    }
}
