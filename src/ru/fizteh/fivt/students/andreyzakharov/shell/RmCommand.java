package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.*;

public class RmCommand extends AbstractCommand {
    RmCommand(Shell shell) {
        super("rm", shell);
    }

    @Override
    public void execute(String... args) {
        boolean recursive;
        if (args.length < 2 || ((recursive = args[1].equals("-r")) && args.length < 3)) {
            shell.error("rm: missing operand");
            return;
        }

        for (int i = (recursive ? 2 : 1); i < args.length; ++i) {
            Path path = shell.wd.resolve(args[i]);
            if (Files.isDirectory(path)) {
                if (!recursive) {
                    shell.error("rm: " + args[i] + ": is a directory");
                    return;
                }
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path file : stream) {
                        if (Files.isDirectory(file)) {
                            execute("rm", "-r", file.toString());
                        } else {
                            try {
                                Files.delete(file);
                            } catch (IOException e) {
                                shell.error("rm: i/o error");
                                return;
                            }
                        }
                    }
                } catch (IOException e) {
                    shell.error("rm: i/o error");
                    return;
                }
            }
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
