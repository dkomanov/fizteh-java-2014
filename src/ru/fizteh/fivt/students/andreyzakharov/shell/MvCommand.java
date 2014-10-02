package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MvCommand extends AbstractCommand {
    MvCommand(Shell shell) {
        super("mv", shell);
    }

    @Override
    public void execute(String... args) {
        if (args.length < 3) {
            shell.error("mv: missing file operand");
            return;
        }
        if (args.length > 3) {
            shell.error("cat: too many arguments");
            return;
        }
        Path src = shell.getWd().resolve(args[1]).normalize();
        Path target = shell.getWd().resolve(args[2]).normalize();

        if (!Files.exists(src)) {
            // note: we do not actually call stat
            shell.error("mv: cannot stat '" + args[1] + "': No such file or directory");
            return;
        }
        if (Files.isDirectory(src) && !Files.isDirectory(target) && Files.exists(target)) {
            shell.error("mv: cannot overwrite non-directory '" + args[2] + "' with directory '" + args[1] + "'");
            return;
        }

        Path dest = Files.isDirectory(target) ? target.resolve(args[2]).normalize() : target;
        String sep = java.nio.file.FileSystems.getDefault().getSeparator();
        try {
            try {
                if ((dest.toString() + sep).startsWith(src.toString() + sep)) {
                    shell.error("mv: cannot move a directory to a subdirectory of itself");
                    return;
                }
                Files.move(src, dest, REPLACE_EXISTING);
            } catch (DirectoryNotEmptyException e) {
                // the folder is not empty and the actual physical move is required
                AbstractCommand cp = new CpCommand(shell);
                AbstractCommand rm = new RmCommand(shell);
                cp.execute("cp", "-r", src.toString(), dest.toString());
                rm.execute("rm", "-r", src.toString(), dest.toString());
            }
        } catch (IOException e) {
            shell.error("mv: i/o error");
        }
    }
}
