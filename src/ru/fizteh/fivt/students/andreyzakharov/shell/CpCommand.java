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
        if (args.length < 3 || ((recursive = args[1].equals("-r")) && args.length < 4)) {
            shell.error("cp: missing file operand");
            return;
        }
        if ((!recursive && args.length > 3) || (recursive && args.length > 4)) {
            shell.error("cp: too many arguments");
            return;
        }

        String srcString = args[recursive ? 2 : 1];
        String targetString = args[recursive ? 3 : 2];

        Path src = shell.getWd().resolve(srcString).normalize();
        if (!Files.exists(src)) {
            // note: we do not actually call stat
            shell.error("cp: cannot stat '" + srcString + "': No such file or directory");
            return;
        }

        boolean isSourceADir = Files.isDirectory(src);
        if (isSourceADir && !recursive) {
            shell.error("cp: " + srcString + " is a directory (not copied).");
            return;
        }

        Path target = shell.getWd().resolve(targetString).normalize();
        boolean isTargetADir = Files.isDirectory(target);

        if (isSourceADir && !isTargetADir && Files.exists(target)) {
            shell.error("cp: cannot overwrite non-directory '" + targetString + "' with directory '" + srcString + "'");
            return;
        }

        Path parent = isTargetADir ? target : target.getParent();
        if (!Files.exists(parent)) {
            shell.error("cp: cannot create " + (isSourceADir ? "directory '" : "regular file '")
                    + targetString + "': No such file or directory");
            return;
        }

        Path dest = (!isTargetADir) ? target : target.resolve(srcString).normalize();

        String sep = java.nio.file.FileSystems.getDefault().getSeparator();
        if (isSourceADir) {
            try {
                if ((dest.toString() + sep).startsWith(src.toString() + sep)) {
                    shell.error("cp: cannot copy a directory into itself");
                    return;
                }
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
                if (Files.exists(dest) && Files.isSameFile(src, dest)) {
                    shell.error("cp: '" + srcString + "' and '" + targetString + "' are the same file");
                    return;
                }
                Files.copy(src, dest, REPLACE_EXISTING);
            } catch (IOException e) {
                shell.error("cp: i/o error");
            }
        }
    }
}
