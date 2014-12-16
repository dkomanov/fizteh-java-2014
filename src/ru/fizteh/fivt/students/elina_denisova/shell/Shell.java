package ru.fizteh.fivt.students.elina_denisova.shell;

import ru.fizteh.fivt.students.elina_denisova.shell.commands.Command;
import ru.fizteh.fivt.students.elina_denisova.shell.commands.*;

import java.io.File;
import java.util.HashMap;


public class Shell extends AbstractShell {
    private File workDirectory;

    private void initWorkDirectory() {
        workDirectory = new File(System.getProperty("user.dir"));
    }

    private void initCommand() {
        commandNames = new HashMap<>();

        addCommand(new PrintWorkDirectoryCommand(this));
        addCommand(new DirectoryListCommand(this));
        addCommand(new ExitCommand());
        addCommand(new MakeDirectoryCommand(this));
        addCommand(new ChangeDirectoryCommand(this));
        addCommand(new PrintFileCommand(this));
        addCommand(new CopyCommand(this));
        addCommand(new RemoveCommand(this));
        addCommand(new MoveCommand(this));
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
