package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CpCommand extends AbstractCommand {
    CpCommand(Shell shell) {
        super("cp", shell);
    }

    @Override
    public void execute(String... args) {
        boolean recursive;
        if (args.length < 2 || ((recursive = args[1].equals("-r")) && args.length < 3)) {
            shell.error("cp: missing file operand");
            return;
        }
        if ((!recursive && args.length > 3) || (recursive && args.length > 4)) {
            shell.error("cp: too many arguments");
            return;
        }

        Path src;
        try {
            src = shell.getWd().resolve(args[recursive ? 2 : 1]).toRealPath();
        } catch (IOException e) {
            // note: we do not actually call stat
            shell.error("cp: cannot stat '" + args[recursive ? 2 : 1] + "': No such file or directory");
            return;
        }

        boolean isSourceADir = Files.isDirectory(src);
        if (isSourceADir && !recursive) {
            shell.error("cp: " + args[1] + " is a directory (not copied).");
            return;
        }

        Path target = shell.getWd().resolve(args[recursive ? 3 : 2]);
        boolean isTargetADir = Files.isDirectory(target);

        Path dest = (!isTargetADir) ? target : target.resolve(src.getFileName());
        if (isSourceADir) {
            try {
                Files.copy(src, dest, REPLACE_EXISTING);
            } catch (IOException e) {
                shell.error("cp: i/o error");
                return;
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(shell.getWd().resolve(src))) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        execute("cp", "-r", src.resolve(file).toString(), dest.resolve(file.getFileName()).toString());
                    } else {
                        Files.copy(src.resolve(file), dest.resolve(file.getFileName()), REPLACE_EXISTING);
                    }
                }
            } catch (IOException e) {
                shell.error("cp: i/o error");
            }
        } else {
            try {
                Files.copy(src, dest, REPLACE_EXISTING);
            } catch (IOException e) {
                shell.error("cp: i/o error");
            }
        }
    }
}
