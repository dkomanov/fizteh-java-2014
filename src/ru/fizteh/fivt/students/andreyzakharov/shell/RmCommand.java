package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class RmCommand extends AbstractCommand {
    RmCommand(Shell shell) {
        super(shell);
        identifier = "rm";
    }

    @Override
    public void execute(String... args) {
        boolean recursive;
        if (args.length < 2 || ((recursive = args[1].equals("-r")) && args.length < 3)) {
            shell.error("rm: missing operand");
            return;
        }

        for (int i = 1; i < args.length; ++i) {
            Path path = shell.wd.resolve(args[i]);
            try {
                Files.delete(path);
            } catch (NoSuchFileException e) {
                shell.error("rm: cannot remove '" + args[i] + "': No such file or directory");
                return;
            } catch (DirectoryNotEmptyException e) {
                shell.error("rm: cannot remove '" + args[i] + "': Directory not empty");
                return;
            } catch (IOException e) {
                shell.error("rm: cannot remove '" + args[i] + "': Permission denied");
                return;
            }
        }
    }
}