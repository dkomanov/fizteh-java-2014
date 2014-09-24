package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.Path;

public class CdCommand extends AbstractCommand {
    CdCommand(Shell shell) {
        super("cd", shell);
    }

    @Override
    public void execute(String... args) {
        String pathString = (args.length < 2) ? System.getProperty("user.home") : args[1];
        try {
            Path newWd = shell.getWd().resolve(pathString).toRealPath();
            shell.setWd(newWd);
        } catch (IOException e) {
            shell.error("cd: '" + pathString + "': No such file or directory");
        }
    }
}
